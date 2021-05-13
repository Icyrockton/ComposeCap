package packetbuild

import ArpData
import EthernetData
import IcmpData
import Ipv4Data
import Ipv6Data
import RawDataASCIIRow
import RawDataRow
import TcpData
import UdpData
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import data.viewModel
import org.pcap4j.packet.*
import kotlinx.coroutines.launch

/**
 *@Author : Icyrockton
 *@Date: 2021/5/12 12:24
 **/
@Composable
fun PacketBuild() {
    val packet by viewModel.packetBuilder.packet.collectAsState()
    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            Row(modifier = Modifier.padding(top = 20.dp)) {
                Button({
                    viewModel.packetBuilder.buildEthernetPacket()
                }, modifier = Modifier.weight(0.25f).height(50.dp).padding(horizontal = 20.dp)) {
                    Text("构造以太帧报文")
                }
                Button({
                    viewModel.packetBuilder.buildIpPacket()

                }, modifier = Modifier.weight(0.25f).height(50.dp).padding(horizontal = 20.dp)) {
                    Text("构造IPv4报文")
                }
                Button({
                    viewModel.packetBuilder.buildTcpPacket()

                }, modifier = Modifier.weight(0.25f).height(50.dp).padding(horizontal = 20.dp)) {
                    Text("构造TCP报文")
                }
                Button({
                    viewModel.packetBuilder.buildUdpPacket()

                }, modifier = Modifier.weight(0.25f).height(50.dp).padding(horizontal = 20.dp)) {
                    Text("构造UDP报文")
                }
            }

            if (packet == null) {

            } else {
                Box(modifier = Modifier.fillMaxWidth().weight(0.5f)) {
                    val scrollState = rememberScrollState()

                    Column(modifier = Modifier.fillMaxSize().padding(end = 12.dp).verticalScroll(scrollState)) {
                        //数据链路层
                        if (packet!!.contains(EthernetPacket::class.java)) {
                            val ethernetPacket = packet!!.get(EthernetPacket::class.java)
                            EthernetData(ethernetPacket)
                            Spacer(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp).height(1.dp)
                                    .background(Color(0xFFE4E4E4))
                            )
                        }
                        //ARP
                        if (packet!!.contains(ArpPacket::class.java)) {
                            val arpPacket = packet!!.get(ArpPacket::class.java)
                            ArpData(arpPacket)
                            Spacer(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp).height(1.dp)
                                    .background(Color(0xFFE4E4E4))
                            )
                        }
                        //网络层
                        if (packet!!.contains(IpV4Packet::class.java)) {
                            val Ipv4Packet = packet!!.get(IpV4Packet::class.java)
                            Ipv4Data(Ipv4Packet)
                            Spacer(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp).height(1.dp)
                                    .background(Color(0xFFE4E4E4))
                            )
                        } else if (packet!!.contains(IpV6Packet::class.java)) {
                            val Ipv6Packet = packet!!.get(IpV6Packet::class.java)
                            Ipv6Data(Ipv6Packet)
                            Spacer(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp).height(1.dp)
                                    .background(Color(0xFFE4E4E4))
                            )
                        }

                        //ICMP
                        if (packet!!.contains(IcmpV4CommonPacket::class.java)) {
                            val icmpV4CommonPacket = packet!!.get(IcmpV4CommonPacket::class.java)
                            IcmpData(icmpV4CommonPacket)
                            Spacer(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp).height(1.dp)
                                    .background(Color(0xFFE4E4E4))
                            )
                        }
                        //传输层
                        if (packet!!.contains(TcpPacket::class.java)) {
                            val tcpPacket = packet!!.get(TcpPacket::class.java)
                            TcpData(tcpPacket)
                            Spacer(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp).height(1.dp)
                                    .background(Color(0xFFE4E4E4))
                            )
                        } else if (packet!!.contains(UdpPacket::class.java)) {
                            val udpPacket = packet!!.get(UdpPacket::class.java)
                            UdpData(udpPacket)
                            Spacer(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp).height(1.dp)
                                    .background(Color(0xFFE4E4E4))
                            )
                        }


                    }
                    VerticalScrollbar(
                        modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                        adapter = rememberScrollbarAdapter(scrollState)
                    )
                }

                //            协议raw数据
                Box(modifier = Modifier.fillMaxWidth().weight(0.5f)) {
                    val scrollState = rememberScrollState()

                    Row(modifier = Modifier.fillMaxSize().padding(end = 12.dp).verticalScroll(scrollState)) {
                        CaptureRawData()
                    }
                    VerticalScrollbar(
                        modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                        adapter = rememberScrollbarAdapter(scrollState)
                    )

                }
            }
        }

    }
}

@Composable
fun CaptureRawData() {
    val packet by viewModel.packetBuilder.packet.collectAsState()
    val byteArrayList = remember { mutableStateListOf<MutableList<Byte>>() }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(packet) {
        println("改变了")
        coroutineScope.launch {
            if (packet != null) {
                packet!!.apply {
                    byteArrayList.clear()
                    val rows = Math.ceil(rawData.size / 16.0).toInt() //总的行数
                    val rawData = rawData
                    var count = 0
                    var byteArray = mutableListOf<Byte>()

                    for (byte in rawData) {
                        byteArray.add(byte)
                        if (byteArray.size == 16) {   //16个字节作为一行
                            byteArrayList.add(byteArray)
                            byteArray = mutableListOf()
                            count += 16
                        }
                    }
                    //                    剩余的字节
                    if (byteArray.size != 0) {
                        byteArrayList.add(byteArray)
                    }
                }
            }
        }
    }
    if (packet != null) {
        packet!!.apply {
            val rows = Math.ceil(rawData.size / 16.0).toInt() //总的行数
            val rawData = rawData
//                字节标识
            Column(modifier = Modifier.fillMaxHeight().background(MaterialTheme.colors.primary)) {
                for (row in 1..rows) {
                    Text(
                        text = String.format("%04x", row * 16),
                        modifier = Modifier.padding(horizontal = 5.dp),
                        color = Color.White
                    )
                }

            }

            Column(modifier = Modifier.fillMaxHeight()) {
                byteArrayList.forEachIndexed { index, byteArray ->
                    RawDataRow(index * 16, byteArray)
                }
            }

            Spacer(modifier = Modifier.width(30.dp).fillMaxHeight())
//            ASCII码显示
            Column(modifier = Modifier.fillMaxHeight()) {
                byteArrayList.forEachIndexed { index, byteArray ->
                    RawDataASCIIRow(index * 16, byteArray)
                }
            }
        }
    }
}