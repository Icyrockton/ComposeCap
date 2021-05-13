package packetbuild

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import data.viewModel

/**
 *@Author : Icyrockton
 *@Date: 2021/5/12 9:55
 **/
@Composable
fun UdpPacketBuilder(packet: UdpBuilderPacket, onChange: (newPacket: UdpBuilderPacket) -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Column() {

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
                "长度",
                packet.length,
                "示例:327",
                onChange = { onChange(packet.copy(length = it)) })


            ParameterRow(
                "校验和",
                packet.checkSum,
                "示例:0x8886",
                onChange = { onChange(packet.copy(checkSum = it)) })

        }


    }
}

data class UdpBuilderPacket(
    val sourcePort: String = "",
    val destinationPort: String = "",
    val length: String = "",
    val checkSum: String = "",
)

@Composable
fun Udp() {
    val packet by viewModel.packetBuilder.udpPacket.collectAsState()
    UdpPacketBuilder(packet) {
        viewModel.packetBuilder.updateUdpPacket(it)
    }


}