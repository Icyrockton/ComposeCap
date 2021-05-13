package packetbuild

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import data.viewModel

/**
 *@Author : Icyrockton
 *@Date: 2021/5/12 9:55
 **/
@Composable
fun EthernetPacketBuilder(
    pakcet: EthernetBuilderPacket,
    onChange: (newPacket: EthernetBuilderPacket) -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Column {
            ParameterRow(
                "目的MAC地址",
                pakcet.destinationAddress,
                "MAC地址 示例:d8:c8:e9:fc:f9:b1",
                onChange = { onChange(pakcet.copy(destinationAddress = it)) })
            ParameterRow(
                "源MAC地址",
                pakcet.sourceAddress,
                "MAC地址 示例:64:5d:86:2b:8c:ed",
                onChange = { onChange(pakcet.copy(sourceAddress = it)) })
            ParameterRow(
                "类型",
                pakcet.type,
                "上层协议类型  示例:0x0800(IPv4)",
                onChange = { onChange(pakcet.copy(type = it)) })
        }
    }
}


data class EthernetBuilderPacket(
    val destinationAddress: String = "",
    val sourceAddress: String = "",
    val type: String = ""
)

@Composable
fun Ethernet() {
    val packet by viewModel.packetBuilder.ethernetPacket.collectAsState()
    EthernetPacketBuilder(
        packet,
        onChange = {
            viewModel.packetBuilder.updateEthernetPacket(it)
        })
}


@Composable
fun ParameterRow(title: String, value: String, placeHolder: String, onChange: (str: String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = title, modifier = Modifier.padding(start = 20.dp).width(200.dp), textAlign = TextAlign.Center)
        OutlinedTextField(
            value,
            onValueChange = onChange,
            maxLines = 1,
            singleLine = true,
            placeholder = { Text(text = placeHolder) })
    }
}
