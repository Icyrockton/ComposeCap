package packetbuild

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import data.viewModel
import org.pcap4j.packet.TcpPacket

/**
 *@Author : Icyrockton
 *@Date: 2021/5/12 9:55
 **/
@Composable
fun TcpPacketBuilder(packet: TcpBuilderPacket, onChange: (newPacket: TcpBuilderPacket) -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Column {
            ParameterRow(
                "源端口",
                packet.sourcePort,
                "示例:5159",
                onChange = { onChange(packet.copy(sourcePort = it)) })


            ParameterRow(
                "目的端口",
                packet.destinationPort,
                "示例:80",
                onChange = { onChange(packet.copy(destinationPort = it)) })

            ParameterRow(
                "序号",
                packet.seqNumber,
                "示例:3516819114",
                onChange = { onChange(packet.copy(seqNumber = it)) })

            ParameterRow(
                "应答号",
                packet.ackNumber,
                "示例:80",
                onChange = { onChange(packet.copy(ackNumber = it)) })

            ParameterRow(
                "紧急指针标志位",
                packet.urgFlag,
                "示例:false",
                onChange = { onChange(packet.copy(urgFlag = it)) })

            ParameterRow(
                "应答标志位",
                packet.ackFlag,
                "示例:false",
                onChange = { onChange(packet.copy(ackFlag = it)) })

            ParameterRow(
                "Push标志位",
                packet.pushFlag,
                "示例:false",
                onChange = { onChange(packet.copy(pushFlag = it)) })

            ParameterRow(
                "Syn标志位",
                packet.synFlag,
                "示例:false",
                onChange = { onChange(packet.copy(synFlag = it)) })

            ParameterRow(
                "Fin终止连接标志位",
                packet.finFlag,
                "示例:false",
                onChange = { onChange(packet.copy(finFlag = it)) })

            ParameterRow(
                "窗口",
                packet.window,
                "示例:516",
                onChange = { onChange(packet.copy(window = it)) })

            ParameterRow(
                "紧急指针",
                packet.urgentPointer,
                "示例:0",
                onChange = { onChange(packet.copy(urgentPointer = it)) })

            ParameterRow(
                "校验和",
                packet.checkSum,
                "示例:0x8886",
                onChange = { onChange(packet.copy(checkSum = it)) })



        }
    }

}

data class TcpBuilderPacket(
    val sourcePort: String = "",
    val destinationPort: String = "",
    val seqNumber: String = "",
    val ackNumber: String = "",
    val headerLength: String = "",
    val urgFlag: String = "",
    val ackFlag: String = "",
    val pushFlag: String = "",
    val resetFlag: String = "",
    val synFlag: String = "",
    val finFlag: String = "",
    val window: String = "",
    val checkSum: String = "",
    val urgentPointer: String = ""
)

@Composable
fun Tcp() {
    val packet by viewModel.packetBuilder.tcpPacket.collectAsState()
    TcpPacketBuilder(packet) { viewModel.packetBuilder.updateTcpPacket(it) }

}