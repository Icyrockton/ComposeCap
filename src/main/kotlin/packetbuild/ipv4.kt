package packetbuild

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import data.viewModel
import org.pcap4j.packet.IpV4Packet
import org.pcap4j.packet.namednumber.IpVersion

/**
 *@Author : Icyrockton
 *@Date: 2021/5/12 9:55
 **/
@Composable
fun Ipv4PacketBuilder(packet: Ipv4BuilderPacket, onChange: (newPacket: Ipv4BuilderPacket) -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Column {
            ParameterRow(
                "版本号",
                packet.version,
                "示例:4(Ipv4版本)",
                onChange = { onChange(packet.copy(version = it)) })
            ParameterRow(
                "首部长度",
                packet.headerLength,
                "MAC地址 示例:5 (代表20字节)",
                onChange = { onChange(packet.copy(headerLength = it)) })
            ParameterRow(
                "总长度",
                packet.totalLength,
                "总长度 示例:40 (代表40字节)",
                onChange = { onChange(packet.copy(totalLength = it)) })
            ParameterRow(
                "标识号",
                packet.identification,
                "数据包标识号 示例:0x4313 (两字节)",
                onChange = { onChange(packet.copy(identification = it)) })
            ParameterRow(
                "保留标识符",
                packet.reservedFlag,
                "false",
                onChange = { onChange(packet.copy(reservedFlag = it)) })
            ParameterRow(
                "不分段标识符",
                packet.dontFragmentFlag,
                "true",
                onChange = { onChange(packet.copy(dontFragmentFlag = it)) })
            ParameterRow(
                "更多分段标识符",
                packet.moreFragmentFlag,
                "false",
                onChange = { onChange(packet.copy(moreFragmentFlag = it)) })
            ParameterRow(
                "片段偏移",
                packet.fragmentOffset,
                "片段偏移 示例:0 ",
                onChange = { onChange(packet.copy(fragmentOffset = it)) })
            ParameterRow(
                "生存时间",
                packet.ttl,
                "TimeToLive 示例:128 ",
                onChange = { onChange(packet.copy(ttl = it)) })
            ParameterRow(
                "协议",
                packet.protocol,
                "上层协议类型 示例:6 (代表TCP) ",
                onChange = { onChange(packet.copy(protocol = it)) })
            ParameterRow(
                "首部校验和",
                packet.headerChecksum,
                "示例:0x0000 (校验关闭)",
                onChange = { onChange(packet.copy(headerChecksum = it)) })
            ParameterRow(
                "源IP地址",
                packet.srcAddr,
                "示例:192.168.123.179",
                onChange = { onChange(packet.copy(srcAddr = it)) })
            ParameterRow(
                "目的IP地址",
                packet.dstAddr,
                "示例:139.224.204.61",
                onChange = { onChange(packet.copy(dstAddr = it)) })
        }
    }
}

data class Ipv4BuilderPacket(
    val version: String = "",
    val headerLength: String = "",
    val differentialServicesField: String = "", // 0x00
    val totalLength: String = "",
    val identification: String = "",
    val reservedFlag: String = "",
    val dontFragmentFlag: String = "",
    val moreFragmentFlag: String = "",
    val fragmentOffset: String = "",
    val ttl: String = "",
    val protocol: String = "",
    val headerChecksum: String = "",
    val srcAddr: String = "",
    val dstAddr: String = "",

    )


@Composable
fun Ipv4() {
    val packet by viewModel.packetBuilder.ipv4Packet.collectAsState()
    Ipv4PacketBuilder(packet) {
        viewModel.packetBuilder.updateIpv4Packet(it)
    }

}