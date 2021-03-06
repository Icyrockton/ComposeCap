import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.shortcuts
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.res.svgResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import data.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.pcap4j.packet.*
import java.util.*
import kotlin.time.measureTime

/**
 *@Author : Icyrockton
 *@Date: 2021/5/10 11:39
 **/
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Capture() {
    val lazyListState = rememberLazyListState()
    LaunchedEffect(viewModel.pcap.captureList.size) {
        if (viewModel.pcap.captureList.size > 0) {
            lazyListState.animateScrollToItem(viewModel.pcap.captureList.size)
        }

    }
    Column(modifier = Modifier.fillMaxSize()) {
        var filterString by remember { mutableStateOf("") }
        OutlinedTextField(
            value = filterString,
            onValueChange = { filterString = it },
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp, end = 10.dp).shortcuts {
                on(Key.Enter) {
                    viewModel.pcap.setFilter(filterString)
                }
            },
            placeholder = { Text(text = "??????BPF(Berkeley Packet Filter)????????????: ??????(tcp port 80) (src/dst host host )  ") },
            leadingIcon = {
                Icon(svgResource("svg/filter.svg"), null, modifier = Modifier.size(40.dp), MaterialTheme.colors.primary)
            },
            singleLine = true,

            )



        Card(modifier = Modifier.fillMaxWidth().padding(top = 20.dp, end = 10.dp).height(500.dp)) {
            Column(Modifier.fillMaxSize()) {
                CaptureListHeader()
                Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    LazyColumn(modifier = Modifier.fillMaxSize().padding(end = 12.dp), state = lazyListState) {
                        items(viewModel.pcap.captureList, { it.no }) {
                            CaptureRow(it)
                        }
                    }
                    VerticalScrollbar(
                        modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                        adapter = ScrollbarAdapter(lazyListState)
                    )
                }
            }
        }
//        ????????????????????????????????????
        Card(modifier = Modifier.fillMaxWidth().padding(top = 20.dp, end = 10.dp).height(500.dp)) {
            CaptureDetail()
        }

    }
}


@Composable
fun CaptureDetail() {
    val selectedPacket by viewModel.pcap.selectedPacket.collectAsState()
    Column(modifier = Modifier.fillMaxSize()) {
        if (selectedPacket == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "??????????????????", style = MaterialTheme.typography.h4)
            }
        } else {
            Box(modifier = Modifier.fillMaxWidth().weight(0.5f)) {
                val scrollState = rememberScrollState()

                Column(modifier = Modifier.fillMaxSize().padding(end = 12.dp).verticalScroll(scrollState)) {
                    //???????????????
                    if (selectedPacket!!.packet.contains(EthernetPacket::class.java)) {
                        val ethernetPacket = selectedPacket!!.packet.get(EthernetPacket::class.java)
                        EthernetData(ethernetPacket)
                        Spacer(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp).height(1.dp)
                                .background(Color(0xFFE4E4E4))
                        )
                    }
                    //ARP
                    if (selectedPacket!!.packet.contains(ArpPacket::class.java)) {
                        val arpPacket = selectedPacket!!.packet.get(ArpPacket::class.java)
                        ArpData(arpPacket)
                        Spacer(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp).height(1.dp)
                                .background(Color(0xFFE4E4E4))
                        )
                    }
                    //?????????
                    if (selectedPacket!!.packet.contains(IpV4Packet::class.java)) {
                        val Ipv4Packet = selectedPacket!!.packet.get(IpV4Packet::class.java)
                        Ipv4Data(Ipv4Packet)
                        Spacer(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp).height(1.dp)
                                .background(Color(0xFFE4E4E4))
                        )
                    } else if (selectedPacket!!.packet.contains(IpV6Packet::class.java)) {
                        val Ipv6Packet = selectedPacket!!.packet.get(IpV6Packet::class.java)
                        Ipv6Data(Ipv6Packet)
                        Spacer(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp).height(1.dp)
                                .background(Color(0xFFE4E4E4))
                        )
                    }

                    //ICMP
                    if (selectedPacket!!.packet.contains(IcmpV4CommonPacket::class.java)) {
                        val icmpV4CommonPacket = selectedPacket!!.packet.get(IcmpV4CommonPacket::class.java)
                        IcmpData(icmpV4CommonPacket)
                        Spacer(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp).height(1.dp)
                                .background(Color(0xFFE4E4E4))
                        )
                    }
                    //?????????
                    if (selectedPacket!!.packet.contains(TcpPacket::class.java)) {
                        val tcpPacket = selectedPacket!!.packet.get(TcpPacket::class.java)
                        TcpData(tcpPacket)
                        Spacer(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp).height(1.dp)
                                .background(Color(0xFFE4E4E4))
                        )
                    } else if (selectedPacket!!.packet.contains(UdpPacket::class.java)) {
                        val udpPacket = selectedPacket!!.packet.get(UdpPacket::class.java)
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
            Spacer(
                modifier = Modifier.fillMaxWidth().height(2.dp).padding(horizontal = 10.dp)
                    .background(Color(0xFFE4E4E4))
            )
//            ??????raw??????
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

//????????????
@Composable
fun CaptureRawData() {
    val fragment by viewModel.pcap.selectedPacketFragment.collectAsState()
    val byteArrayList = remember { mutableStateListOf<MutableList<Byte>>() }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(fragment) {
        coroutineScope.launch {
            if (fragment != null) {
                fragment!!.apply {
                    byteArrayList.clear()
                    val rows = Math.ceil(packet.rawData.size / 16.0).toInt() //????????????
                    val rawData = packet.rawData
                    var count = 0
                    var byteArray = mutableListOf<Byte>()

                    for (byte in rawData) {
                        byteArray.add(byte)
                        if (byteArray.size == 16) {   //16?????????????????????
                            byteArrayList.add(byteArray)
                            byteArray = mutableListOf()
                            count += 16
                        }
                    }
                    //                    ???????????????
                    if (byteArray.size != 0) {
                        byteArrayList.add(byteArray)
                    }
                }
            }
        }
    }
    if (fragment != null) {
        fragment!!.apply {
            val rows = Math.ceil(packet.rawData.size / 16.0).toInt() //????????????
            val rawData = packet.rawData
//                ????????????
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
//            ASCII?????????
            Column(modifier = Modifier.fillMaxHeight()) {
                byteArrayList.forEachIndexed { index, byteArray ->
                    RawDataASCIIRow(index * 16, byteArray)
                }
            }
        }
    }
}

@Composable
fun RawDataRow(startIndex: Int, byteArray: List<Byte>) {
    val highLight by viewModel.pcap.highLight.collectAsState()
    Row() {
        byteArray.forEachIndexed { index, byte ->
            val finalIndex = index + startIndex
            if (index == 8)
                Spacer(modifier = Modifier.width(60.dp))
            if (highLight != null && (highLight!!.start <= finalIndex) && (finalIndex < (highLight!!.start + highLight!!.length))) {
//                ??????
                Text(
                    text = String.format("%02x", byte),
                    modifier = Modifier.width(30.dp).background(Color(0xff0078D7)),
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            } else {
                Text(text = String.format("%02x", byte), modifier = Modifier.width(30.dp), textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
fun RawDataASCIIRow(startIndex: Int, byteArray: List<Byte>) {
    val highLight by viewModel.pcap.highLight.collectAsState()

    Row {
        byteArray.forEachIndexed { index, byte ->
            val finalIndex = index + startIndex
            val friendlyASCII = byte.isFriendlyASCII()
            if (highLight != null && (highLight!!.start <= finalIndex) && (finalIndex < (highLight!!.start + highLight!!.length))) {
                Text(
                    text = "${if (friendlyASCII) byte.toChar() else '??'}",
                    modifier = Modifier.width(12.dp).background(Color(0xff0078D7)),
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            } else {
                Text(
                    text = "${if (friendlyASCII) byte.toChar() else '??'}",
                    modifier = Modifier.width(12.dp),
                    textAlign = TextAlign.Center,
                    color = if (friendlyASCII) Color.Black else Color(0XFFA4A9BC)
                )
            }
        }
    }
}

fun Byte.isFriendlyASCII(): Boolean {
    if (this < 33 || this > 126)
        return false
    return true
}


@Composable
fun HoverText(text: String, modifier: Modifier = Modifier, onClick: (() -> Unit) = {}) {
    var hovered by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxWidth()
            .pointerMoveFilter(onEnter = { hovered = true;false }, onExit = { hovered = false;false })
            .background(if (hovered) Color(0xFFCCE8FF) else Color.White).clickable { onClick() }
            .then(modifier), verticalArrangement = Arrangement.Center
    ) {
        Text(text = text)
    }
}


@Composable
fun EthernetData(data: EthernetPacket) {
    val coroutineScope = rememberCoroutineScope()

    ProtocolRow("???????????????(Ethernet ???) , ???MAC??????: ${data.header.srcAddr} , ??????MAC??????: ${data.header.dstAddr}") {
        Column(modifier = Modifier.padding(start = 70.dp)) {
            HoverText(text = "??????MAC??????(Destination): ${data.header.dstAddr}") {
                coroutineScope.launch {
                    viewModel.pcap.ethernetFragmentHighLight(EthernetType.destination)
                }
            }
            HoverText(text = "???MAC??????(Source): ${data.header.srcAddr}", modifier = Modifier.padding(top = 5.dp)) {
                coroutineScope.launch {
                    viewModel.pcap.ethernetFragmentHighLight(EthernetType.srouce)
                }
            }
            HoverText(
                text = "??????(Type): ${data.header.type.name()} (${data.header.type.valueAsString()})",
                modifier = Modifier.padding(top = 5.dp)
            ) {
                coroutineScope.launch {
                    viewModel.pcap.ethernetFragmentHighLight(EthernetType.type)
                }
            }
        }
    }
}


@Composable
fun ArpData(arpPacket: ArpPacket) {
    val coroutineScope = rememberCoroutineScope()

    ProtocolRow(
        "??????????????????(Address Resolution Protocol) (${
            arpPacket.header.operation.value().toString().toLowerCase()
        })"
    ) {
        Column(modifier = Modifier.padding(start = 70.dp)) {
            HoverText(text = "????????????(Hardware type): ${arpPacket.header.hardwareType.value()}") {
                coroutineScope.launch {
                    viewModel.pcap.arpFragmentHighLight(ArpType.HardWareType)
                }
            }
            HoverText(text = "????????????(Protocol type): ${arpPacket.header.protocolType.value()}") {
                coroutineScope.launch {
                    viewModel.pcap.arpFragmentHighLight(ArpType.ProtocolType)

                }
            }
            HoverText(
                text = "??????????????????(Hardware size): ${arpPacket.header.hardwareAddrLengthAsInt}",
                modifier = Modifier.padding(top = 5.dp)
            ) {
                coroutineScope.launch {
                    viewModel.pcap.arpFragmentHighLight(ArpType.HardWareAddrLength)

                }
            }
            HoverText(
                text = "??????????????????(Protocol size): ${arpPacket.header.protocolAddrLengthAsInt}",
                modifier = Modifier.padding(top = 5.dp)
            ) {
                coroutineScope.launch {
                    viewModel.pcap.arpFragmentHighLight(ArpType.ProtocolAddrLength)

                }
            }
            HoverText(
                text = "????????????(Opcode): ${arpPacket.header.operation.value()}",
                modifier = Modifier.padding(top = 5.dp)
            ) {
                coroutineScope.launch {
                    viewModel.pcap.arpFragmentHighLight(ArpType.Operation)

                }
            }
            HoverText(
                text = "????????? MAC ??????(Sender MAC address): ${arpPacket.header.srcHardwareAddr}",
                modifier = Modifier.padding(top = 5.dp)
            ) {
                coroutineScope.launch {
                    viewModel.pcap.arpFragmentHighLight(ArpType.SrcHardWareAddr)
                }
            }
            HoverText(
                text = "????????? IP ??????(Sender IP address): ${arpPacket.header.srcProtocolAddr.hostAddress}",
                modifier = Modifier.padding(top = 5.dp)
            ) {
                coroutineScope.launch {
                    viewModel.pcap.arpFragmentHighLight(ArpType.SrcProtocolAddr)

                }
            }
            HoverText(
                text = "?????? MAC ??????(Target MAC address): ${arpPacket.header.dstHardwareAddr}",
                modifier = Modifier.padding(top = 5.dp)
            ) {
                coroutineScope.launch {
                    viewModel.pcap.arpFragmentHighLight(ArpType.DstHardWareAddr)

                }
            }
            HoverText(
                text = "?????? IP ??????(Target IP address): ${arpPacket.header.dstProtocolAddr.hostAddress}",
                modifier = Modifier.padding(top = 5.dp)
            ) {
                coroutineScope.launch {
                    viewModel.pcap.arpFragmentHighLight(ArpType.DstProtocolAddr)
                }
            }
        }
    }
}

@Composable
fun Ipv4Data(data: IpV4Packet) {
    val coroutineScope = rememberCoroutineScope()

    ProtocolRow("??????V4??????(Internet Protocol Version 4) , ???IP??????: ${data.header.srcAddr.hostAddress} , ??????IP??????: ${data.header.dstAddr.hostAddress}") {
        Column(modifier = Modifier.padding(start = 70.dp)) {
            HoverText(text = "?????????(Version): ${data.header.version.name()} (${data.header.version.value()})") {
                coroutineScope.launch {
                    viewModel.pcap.ipv4FragmentHighLight(Ipv4Type.Version)
                }
            }
            HoverText(
                text = "????????????(Header Length): ${data.header.ihlAsInt * 4} ?????? (${data.header.ihlAsInt})",
                modifier = Modifier.padding(top = 5.dp)
            ) {
                coroutineScope.launch {
                    viewModel.pcap.ipv4FragmentHighLight(Ipv4Type.HeaderLength)

                }
            }
            HoverText(
                text = "????????????(Differential Services Field): ${data.header.tos.value()}",
                modifier = Modifier.padding(top = 5.dp)
            ) {
                coroutineScope.launch {
                    viewModel.pcap.ipv4FragmentHighLight(Ipv4Type.DifferentialServicesField)

                }
            }
            HoverText(
                text = "?????????(Total Length): ${data.header.totalLengthAsInt}",
                modifier = Modifier.padding(top = 5.dp)
            ) {
                coroutineScope.launch {
                    viewModel.pcap.ipv4FragmentHighLight(Ipv4Type.TotalLength)

                }
            }
            HoverText(
                text = "?????????(Identification): ${
                    String.format(
                        "0x%x",
                        data.header.identification
                    )
                } (${data.header.identification})", modifier = Modifier.padding(top = 5.dp)
            ) {
                coroutineScope.launch {
                    viewModel.pcap.ipv4FragmentHighLight(Ipv4Type.Identification)

                }
            }
            ProtocolRow("?????????(Flags)", true) {
                Column(modifier = Modifier.padding(start = 50.dp)) {
                    HoverText(
                        text = "?????????(Reserved Bit): ${data.header.reservedFlag}",
                        modifier = Modifier.padding(top = 5.dp)
                    ) {
                        coroutineScope.launch {
                            viewModel.pcap.ipv4FragmentHighLight(Ipv4Type.Flags)

                        }
                    }
                    HoverText(
                        text = "?????????(Don't Fragment): ${data.header.dontFragmentFlag}",
                        modifier = Modifier.padding(top = 5.dp)
                    ) {
                        coroutineScope.launch {
                            viewModel.pcap.ipv4FragmentHighLight(Ipv4Type.Flags)

                        }
                    }
                    HoverText(
                        text = "????????????(More Fragment): ${data.header.moreFragmentFlag}",
                        modifier = Modifier.padding(top = 5.dp)
                    ) {
                        coroutineScope.launch {
                            viewModel.pcap.ipv4FragmentHighLight(Ipv4Type.Flags)

                        }
                    }
                }
            }
            HoverText(text = "????????????(Fragment): ${data.header.fragmentOffset}", modifier = Modifier.padding(top = 5.dp)) {
                coroutineScope.launch {
                    viewModel.pcap.ipv4FragmentHighLight(Ipv4Type.FragmentOffset)

                }
            }
            HoverText(text = "????????????(Time to Live): ${data.header.ttlAsInt}", modifier = Modifier.padding(top = 5.dp)) {
                coroutineScope.launch {
                    viewModel.pcap.ipv4FragmentHighLight(Ipv4Type.TimeToLive)

                }
            }
            HoverText(
                text = "??????(Protocol): ${data.header.protocol.name()} (${data.header.protocol.value()})",
                modifier = Modifier.padding(top = 5.dp)
            ) {
                coroutineScope.launch {
                    viewModel.pcap.ipv4FragmentHighLight(Ipv4Type.Protocol)

                }
            }
            HoverText(
                text = "???????????????(Header Checksum): ${data.header.headerChecksum}",
                modifier = Modifier.padding(top = 5.dp)
            ) {
                coroutineScope.launch {
                    viewModel.pcap.ipv4FragmentHighLight(Ipv4Type.HeaderCheckSum)

                }
            }
            HoverText(
                text = "?????????(Source Address): ${data.header.srcAddr.hostAddress}",
                modifier = Modifier.padding(top = 5.dp)
            ) {
                coroutineScope.launch {
                    viewModel.pcap.ipv4FragmentHighLight(Ipv4Type.SourceAddress)

                }
            }
            HoverText(
                text = "????????????(Destination Address): ${data.header.dstAddr.hostAddress}",
                modifier = Modifier.padding(top = 5.dp)
            ) {
                coroutineScope.launch {
                    viewModel.pcap.ipv4FragmentHighLight(Ipv4Type.DestinationAddress)
                }
            }
        }
    }
}

@Composable
fun Ipv6Data(Ipv6Packet: IpV6Packet) {

}

@Composable
fun IcmpData(data: IcmpV4CommonPacket) {
    val coroutineScope = rememberCoroutineScope()

    ProtocolRow("????????????????????????(Internet Protocol Message Protocol)") {
        Column(modifier = Modifier.padding(start = 70.dp)) {

            HoverText(text = "??????(Type): ${data.header.type.value()} (${data.header.type.name()})") {
                coroutineScope.launch {
                    viewModel.pcap.icmpFragmentHighLight(IcmpType.Type)
                }
            }
            HoverText(text = "??????(Code): ${data.header.code.value()} (${data.header.type.name()})") {
                coroutineScope.launch {
                    viewModel.pcap.icmpFragmentHighLight(IcmpType.Code)

                }
            }
            HoverText(text = "?????????(Checksum): ${data.header.checksum} ") {
                coroutineScope.launch {
                    viewModel.pcap.icmpFragmentHighLight(IcmpType.Checksum)

                }
            }


//        ????????????
            data.containsAndDoCompose(IcmpV4EchoPacket::class.java) {
                HoverText(text = "??????(Identifier): ${it.header.identifier}") {
                    coroutineScope.launch {
                        viewModel.pcap.icmpFragmentHighLight(IcmpType.Identifier)

                    }
                }
                HoverText(text = "??????(Sequence Number): ${it.header.sequenceNumber}") {
                    coroutineScope.launch {
                        viewModel.pcap.icmpFragmentHighLight(IcmpType.SeqNumber)

                    }
                }
                HoverText(text = "??????(Data) (${it.payload.length()})") {
                    coroutineScope.launch {
                        viewModel.pcap.icmpFragmentHighLight(IcmpType.Payload)

                    }
                }
            }

            data.containsAndDoCompose(IcmpV4EchoReplyPacket::class.java) {
                HoverText(text = "??????(Identifier): ${it.header.identifier}") {
                    coroutineScope.launch {
                        viewModel.pcap.icmpFragmentHighLight(IcmpType.Identifier)

                    }
                }
                HoverText(text = "??????(Sequence Number): ${it.header.sequenceNumber}") {
                    coroutineScope.launch {
                        viewModel.pcap.icmpFragmentHighLight(IcmpType.SeqNumber)

                    }
                }
                HoverText(text = "??????(Data) (${it.payload.length()})") {
                    coroutineScope.launch {
                        viewModel.pcap.icmpFragmentHighLight(IcmpType.Payload)

                    }
                }
            }

            data.containsAndDoCompose(IcmpV4DestinationUnreachablePacket::class.java) {
                HoverText(text = "?????????(Unused) (${it.header.unused})") {
                    coroutineScope.launch {
                        viewModel.pcap.icmpFragmentHighLight(IcmpType.Unused)

                    }
                }
                HoverText(text = "??????(Data) (${it.payload.length()})") {
                    coroutineScope.launch {
                        viewModel.pcap.icmpFragmentHighLight(IcmpType.Payload)

                    }
                }
            }
        }
    }


}

@Composable
fun TcpData(data: TcpPacket) {
    val coroutineScope = rememberCoroutineScope()
    ProtocolRow("??????????????????(Transmission Control Protocol) , ?????????: (${data.header.srcPort.value()}) , ????????????: (${data.header.dstPort.value()})") {
        Column(modifier = Modifier.padding(start = 70.dp)) {
            HoverText(text = "?????????(Source Port): ${data.header.srcPort.name()} (${data.header.srcPort.value()})") {
                coroutineScope.launch {
                    viewModel.pcap.tcpFragmentHighLight(TcpType.SourcePort)
                }
            }
            HoverText(
                text = "????????????(Destination Port): ${data.header.dstPort.name()} (${data.header.dstPort.value()})",
                modifier = Modifier.padding(top = 5.dp)
            ) {
                coroutineScope.launch {
                    viewModel.pcap.tcpFragmentHighLight(TcpType.DestinationPort)

                }
            }
            HoverText(
                text = "??????(Sequence Number): ${data.header.sequenceNumber}",
                modifier = Modifier.padding(top = 5.dp)
            ) {
                coroutineScope.launch {
                    viewModel.pcap.tcpFragmentHighLight(TcpType.SeqNumber)

                }
            }
            HoverText(
                text = "?????????(Acknowledgement Number): ${data.header.acknowledgmentNumber}",
                modifier = Modifier.padding(top = 5.dp)
            ) {
                coroutineScope.launch {
                    viewModel.pcap.tcpFragmentHighLight(TcpType.AckNumber)

                }
            }
            HoverText(
                text = "????????????(Data Offset): ${data.header.dataOffsetAsInt}",
                modifier = Modifier.padding(top = 5.dp)
            ) {
                coroutineScope.launch {
                    viewModel.pcap.tcpFragmentHighLight(TcpType.DataOffset)

                }
            }
            ProtocolRow("?????????(Flags)", true) {
                Column(modifier = Modifier.padding(start = 50.dp)) {
                    HoverText(text = "??????????????????(Urgent): ${data.header.urg}") {
                        coroutineScope.launch {
                            viewModel.pcap.tcpFragmentHighLight(TcpType.Flags)

                        }
                    }
                    HoverText(
                        text = "??????????????????(Acknowledgment): ${data.header.ack}",
                        modifier = Modifier.padding(top = 5.dp)
                    ) {
                        coroutineScope.launch {
                            viewModel.pcap.tcpFragmentHighLight(TcpType.Flags)

                        }
                    }
                    HoverText(text = "?????????????????????(Push): ${data.header.psh}", modifier = Modifier.padding(top = 5.dp)) {
                        coroutineScope.launch {
                            viewModel.pcap.tcpFragmentHighLight(TcpType.Flags)

                        }
                    }
                    HoverText(text = "??????????????????(Reset): ${data.header.rst}", modifier = Modifier.padding(top = 5.dp)) {
                        coroutineScope.launch {
                            viewModel.pcap.tcpFragmentHighLight(TcpType.Flags)

                        }
                    }
                    HoverText(text = "??????????????????(Syn): ${data.header.syn}", modifier = Modifier.padding(top = 5.dp)) {
                        coroutineScope.launch {
                            viewModel.pcap.tcpFragmentHighLight(TcpType.Flags)

                        }
                    }
                    HoverText(text = "??????????????????(Fin): ${data.header.fin}", modifier = Modifier.padding(top = 5.dp)) {
                        coroutineScope.launch {
                            viewModel.pcap.tcpFragmentHighLight(TcpType.Flags)

                        }
                    }
                }
            }
            HoverText(text = "??????(Window): ${data.header.window}", modifier = Modifier.padding(top = 5.dp)) {
                coroutineScope.launch {
                    viewModel.pcap.tcpFragmentHighLight(TcpType.Window)

                }
            }
            HoverText(text = "?????????(Checksum): ${data.header.checksum}", modifier = Modifier.padding(top = 5.dp)) {
                coroutineScope.launch {
                    viewModel.pcap.tcpFragmentHighLight(TcpType.Checksum)

                }
            }
            HoverText(
                text = "????????????(Urgent Pointer): ${data.header.urgentPointer}",
                modifier = Modifier.padding(top = 5.dp)
            ) {
                coroutineScope.launch {
                    viewModel.pcap.tcpFragmentHighLight(TcpType.UrgentPointer)

                }
            }
            if (data.header.options.isNotEmpty()) {
                ProtocolRow("??????(Options)", true) {
                    Column(modifier = Modifier.padding(start = 50.dp)) {
                        data.header.options.forEachIndexed { index, option ->
                            if (index == 0)
                                HoverText(text = "${option.kind.name()}: ${option.rawData.toHexString()}") {
                                    coroutineScope.launch {
                                        viewModel.pcap.tcpFragmentHighLight(TcpType.Options)

                                    }
                                }
                            else
                                HoverText(
                                    text = "${option.kind.name()}: ${option.rawData.toHexString()}",
                                    modifier = Modifier.padding(top = 5.dp)
                                ) {
                                    coroutineScope.launch {
                                        viewModel.pcap.tcpFragmentHighLight(TcpType.Options)

                                    }
                                }

                        }
                    }
                }
            }
            HoverText(
                text = "??????(TCP payload) (${data.payload?.length() ?: 0} ??????)",
                modifier = Modifier.padding(top = 5.dp)
            ) {
                coroutineScope.launch {
                    viewModel.pcap.tcpFragmentHighLight(TcpType.Payload)
                }
            }

        }
    }
}

@Composable
fun UdpData(data: UdpPacket) {
    val coroutineScope = rememberCoroutineScope()

    ProtocolRow("?????????????????????(User Datagram Protocol) , ?????????: (${data.header.srcPort.value()}) , ????????????: (${data.header.dstPort.value()})") {
        Column(modifier = Modifier.padding(start = 70.dp)) {
            HoverText(text = "?????????(Source Port): ${data.header.srcPort.name()} (${data.header.srcPort.value()})") {
                coroutineScope.launch {
                    viewModel.pcap.udpFragmentHighLight(UdpType.SourcePort)
                }
            }
            HoverText(
                text = "????????????(Destination Port): ${data.header.dstPort.name()} (${data.header.dstPort.value()})",
                modifier = Modifier.padding(top = 5.dp)
            ) {
                coroutineScope.launch {
                    viewModel.pcap.udpFragmentHighLight(UdpType.DestinationPort)

                }
            }
            HoverText(
                text = "??????(Length): ${data.header.length}",
                modifier = Modifier.padding(top = 5.dp)
            ) {
                coroutineScope.launch {
                    viewModel.pcap.udpFragmentHighLight(UdpType.Length)

                }
            }
            HoverText(
                text = "?????????(Checksum): ${data.header.checksum}",
                modifier = Modifier.padding(top = 5.dp)
            ) {
                coroutineScope.launch {
                    viewModel.pcap.udpFragmentHighLight(UdpType.Checksum)

                }
            }
            HoverText(
                text = "??????(UDP payload) (${data.payload?.length() ?: 0} ??????)",
                modifier = Modifier.padding(top = 5.dp)
            ) {
                coroutineScope.launch {
                    viewModel.pcap.udpFragmentHighLight(UdpType.Payload)
                }
            }
        }
    }
}

@Composable
fun ProtocolRow(title: String, innerRow: Boolean = false, content: @Composable () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val rotate = remember { Animatable(-90.0f) }
    var hovered by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(expanded) {
        coroutineScope.launch {
            if (expanded) {
                rotate.animateTo(0f)
            } else {
                rotate.animateTo(-90.0f)
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth().animateContentSize()) {
        Row(modifier = Modifier.fillMaxWidth().height(40.dp), verticalAlignment = Alignment.CenterVertically) {
            if (!innerRow) {
                Spacer(modifier = Modifier.width(20.dp))
            }
            IconButton(modifier = Modifier.size(40.dp), onClick = { expanded = !expanded }) {
                Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.rotate(rotate.value))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                modifier = Modifier.weight(1.0f).fillMaxHeight()
                    .pointerMoveFilter(onEnter = { hovered = true;false }, onExit = { hovered = false;false })
                    .background(if (hovered) Color(0xFFCCE8FF) else Color.White),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                )
            }

        }
        if (expanded) {
            content()
        }
    }
}

@Composable
fun CaptureRow(packet: PacketDetail) {
    val averageWeight = remember { 1.0f / svgToTitle.size }
    var active by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    Row(
        modifier = Modifier.fillMaxWidth().height(25.dp)
            .background(
                if (active) {
                    when (packet.type) {
                        PacketType.ARP -> Color(0xFFC8DCDF)
                        PacketType.ICMP -> Color(0xFFCACFFF)
                        PacketType.UDP -> Color(0xFFAEDAFF)
                        PacketType.DNS -> Color(0xFFAEDAFF)
                        PacketType.TCP -> Color(0xFFB6E8D2)
                        PacketType.HTTP -> Color(0xFFB6E8D2)
                    }
                } else {
                    when (packet.type) {
                        PacketType.ARP -> Color(0xFFFAF0D7)
                        PacketType.ICMP -> Color(0xFFFCE0FF)
                        PacketType.UDP -> Color(0xFFDAEEFF)
                        PacketType.DNS -> Color(0xFFDAEEFF)
                        PacketType.TCP -> Color(0xFFE4FFC7)
                        PacketType.HTTP -> Color(0xFFE4FFC7)
                    }
                }
            )
            .pointerMoveFilter(onEnter = { active = true; false }, onExit = { active = false;false }).clickable {
                coroutineScope.launch {
                    viewModel.pcap.setSelectedPacket(packet)
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        with(packet) {

            Text(
                text = packet.no.toString(),
                modifier = Modifier.weight(averageWeight),
                textAlign = TextAlign.Center
            )
            val dateTime =
                Instant.fromEpochMilliseconds(packet.time).toLocalDateTime(TimeZone.currentSystemDefault())
            Text(
                text = "${dateTime.hour}:${dateTime.minute}:${dateTime.second}",
                modifier = Modifier.weight(averageWeight),
                textAlign = TextAlign.Center
            )
//           ???????????????
            if (packet is IPv4PacketDetail) {
                Text(
                    text = packet.source.hostAddress,
                    modifier = Modifier.weight(averageWeight),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = packet.destination.hostAddress,
                    modifier = Modifier.weight(averageWeight),
                    textAlign = TextAlign.Center
                )
            } else if (packet is ArpPacketDetail) {
                Text(
                    text = packet.source.toString(),
                    modifier = Modifier.weight(averageWeight),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = packet.destination.toString(),
                    modifier = Modifier.weight(averageWeight),
                    textAlign = TextAlign.Center
                )
            } else if (packet is IPv6PacketDetail) {
                Text(
                    text = packet.source.hostAddress,
                    modifier = Modifier.weight(averageWeight),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = packet.destination.hostAddress,
                    modifier = Modifier.weight(averageWeight),
                    textAlign = TextAlign.Center
                )
            }
            Text(text = packet.type.name, modifier = Modifier.weight(averageWeight), textAlign = TextAlign.Center)
            Text(
                text = packet.packet.rawData.size.toString(),
                modifier = Modifier.weight(averageWeight),
                textAlign = TextAlign.Center
            )

        }
    }

}

val svgToTitle =
    listOf(
        "??????" to "svg/no.svg",
        "??????" to "svg/time.svg",
        "?????????" to "svg/ip.svg",
        "????????????" to "svg/ip.svg",
        "??????" to "svg/protocol.svg",
        "??????" to "svg/length.svg",
    )

//????????????
@Composable
fun CaptureListHeader() {

    val averageWeight by derivedStateOf { 1.0f / svgToTitle.size }
    Row(modifier = Modifier.fillMaxWidth()) {
        svgToTitle.forEach {
            CaptureListHeaderItem(svgResource(it.second), it.first, Modifier.weight(averageWeight))
        }
    }
}

@Composable
fun CaptureListHeaderItem(svgImage: Painter, title: String, modifier: Modifier = Modifier) {
    Box(modifier = Modifier.padding(0.dp, 10.dp).then(modifier)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
        ) {
            Icon(
                painter = svgImage,
                contentDescription = "listHeaderItem",
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )
            Text(text = title)
        }
    }
}


fun ByteArray.toHexString(): String {
    val formatter = Formatter()
    for (byte in this) {
        formatter.format("%02x", byte)
    }
    val result: String = formatter.toString()
    formatter.close()
    return "0x${result}"
}