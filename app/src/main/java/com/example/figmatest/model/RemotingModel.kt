package com.example.figmatest.model

import com.example.figmatest.enums.EngineCommand
import com.example.figmatest.imt.base.core.serialization.SerializableIfc
import com.example.figmatest.imt.base.lib.remoting.DataReceiverIfc
import com.example.figmatest.imt.base.lib.remoting.DataSenderIfc
import com.example.figmatest.imt.base.lib.remoting.layers.crc16check.Crc16CheckFailedCallbackIfc
import com.example.figmatest.imt.base.lib.remoting.layers.crc16check.Crc16CheckReceiveLayer
import com.example.figmatest.imt.base.lib.remoting.layers.crc16check.Crc16CheckSendLayer
import com.example.figmatest.imt.base.lib.remoting.layers.framesync.FrameSyncReceiveLayer
import com.example.figmatest.imt.base.lib.remoting.layers.framesync.FrameSyncSendLayer
import com.example.figmatest.imt.base.lib.remoting.service.GenericReceiverIfc
import com.example.figmatest.imt.base.lib.remoting.service.GenericRemoteObject
import com.example.figmatest.imt.base.lib.remoting.service.RemotingReceiveService
import com.example.figmatest.imt.base.lib.remoting.service.RemotingSendService
import com.example.figmatest.protocol.EngineCommandProtocol
import com.example.figmatest.protocol.EngineSettingsProtocol
import com.example.figmatest.protocol.PatientSettingsProtocol
import com.example.figmatest.protocol.ProtocolIdentifier
import com.example.figmatest.protocol.VitalSignsDataProtocol
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.ByteBuffer

abstract class RemotingModel() : RemoteDataProducer(), Crc16CheckFailedCallbackIfc, DataReceiverIfc, GenericReceiverIfc {

    private var remotingReceiveService: RemotingReceiveService? = null
    private var remotingSendService: RemotingSendService? = null
    private var crc16CheckSendLayer: Crc16CheckSendLayer? = null
    private var crc16CheckReceiveLayer: Crc16CheckReceiveLayer? = null
    private var frameSyncSendLayer: FrameSyncSendLayer? = null
    private var frameSyncReceiveLayer: FrameSyncReceiveLayer? = null
    private var engineCommandRemoteObject: GenericRemoteObject? = null
    private var engineSettingsRemoteObject: GenericRemoteObject? = null
    private var vitalSignsDataRemoteObject: GenericRemoteObject? = null

    init {
        // remoting receive stack
        remotingReceiveService = RemotingReceiveService()
        crc16CheckReceiveLayer = Crc16CheckReceiveLayer(remotingReceiveService, this)
        crc16CheckReceiveLayer!!.setInitialCrcValue(0xFFFF.toShort());
        frameSyncReceiveLayer = FrameSyncReceiveLayer(2048, crc16CheckReceiveLayer)

        // remoting send stack
        frameSyncSendLayer = FrameSyncSendLayer(2048)
        crc16CheckSendLayer = Crc16CheckSendLayer(frameSyncSendLayer)
        crc16CheckSendLayer!!.setInitialCrcValue(0xFFFF.toShort())
        remotingSendService = RemotingSendService(2048, crc16CheckSendLayer)

        vitalSignsDataRemoteObject = GenericRemoteObject(this, VitalSignsDataProtocol.builder().build())

        engineSettingsRemoteObject = GenericRemoteObject(null, null)
        engineCommandRemoteObject = GenericRemoteObject(null, null)

        remotingReceiveService!!.add(vitalSignsDataRemoteObject, ProtocolIdentifier.VITAL_SIGNS_DATA)

        remotingSendService!!.add(engineCommandRemoteObject, ProtocolIdentifier.ENGINE_COMMAND)
        remotingSendService!!.add(engineSettingsRemoteObject, ProtocolIdentifier.ENGINE_SETTINGS)

    }

    override fun onCrc16CheckFailed(receiveBuffer: ByteBuffer?) {
        //TODO("Not yet implemented")
    }

    override fun onDataReceived(receiveBuffer: ByteBuffer?) {
        frameSyncReceiveLayer?.onDataReceived(receiveBuffer)
    }

    override fun onDataReceived(data: SerializableIfc?) {
        CoroutineScope(Dispatchers.Main).launch {
            when (data) {
                is VitalSignsDataProtocol -> {
                    processData(data)
                }
            }
        }
    }

    fun setLowerLevelSender(dataSender: DataSenderIfc) {
        frameSyncSendLayer?.setLowerLevelSender(dataSender)
    }
    
    fun sendCommand(command: EngineCommand) {
        var engineCommandProtocolBuilder: EngineCommandProtocol.Builder = EngineCommandProtocol.builder()
        engineCommandRemoteObject?.setDataToSend(engineCommandProtocolBuilder.engineCommand(command.value.toShort()).build())
        remotingSendService?.send(ProtocolIdentifier.ENGINE_COMMAND)
    }

    fun sendSettings() {
        var engineSettingsProtocolBuilder: EngineSettingsProtocol.Builder = EngineSettingsProtocol.builder()
        var patientSettingsProtocolBulder: PatientSettingsProtocol.Builder = PatientSettingsProtocol.builder()

        engineSettingsRemoteObject?.setDataToSend(engineSettingsProtocolBuilder.patientSettings(patientSettingsProtocolBulder.sex(1)
            .age(18)
            .weight(50.0)
            .height(160.0)
            .bodyFatFraction(0.179)
            .diastolicArterialPressureBaseline(75.0)
            .heartRateBaseline(70.0)
            .respirationRateBaseline(10.0)
            .systolicArterialPressureBaseline(118.0)
            .pulsePressureBaseline(40.0) // sys - dia
            .meanArterialPressure(86.0)
            .rightLungRatio(0.53)
            .heartRateMaximum(180.0)
            .heartRateMinimum(10.0)
            .totalLungCapacity(4.0)
            .bloodVolumeBaseline(3.368)
            .functionalResidualCapacity(1.500)
            .residualVolume(0.8)
            .basalMetabolicRate(1300.0)
            .alveoliSurfaceArea(46.5)
            .skinSurfaceArea(1.4).build())
            .build())
        remotingSendService?.send(ProtocolIdentifier.ENGINE_SETTINGS)
    }
}