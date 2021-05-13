package data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.pcap4j.core.*
import org.pcap4j.packet.*
import org.pcap4j.util.MacAddress
import packetbuild.PacketViewModel
import java.net.Inet4Address
import java.net.Inet6Address

/**
 *@Author : Icyrockton
 *@Date: 2021/5/10 12:49
 **/

class PcapViewModel {
    //    当前的网络接口
    private val _networkInterfaces = MutableStateFlow<List<NetworkInterfaceMicrosoft>>(emptyList())
    val networkInterfaces: StateFlow<List<NetworkInterfaceMicrosoft>> = _networkInterfaces

    //    当前的捕捉网卡
    private val _currentNetworkInterface = MutableStateFlow<PcapNetworkInterface?>(null)
    val currentNetworkInterface: StateFlow<PcapNetworkInterface?> = _currentNetworkInterface

    //    网卡的详细信息
    private val _currentNetworkInterfaceDetail = MutableStateFlow<NetworkInterfaceDetail?>(null)
    val currentNetworkInterfaceDetail: StateFlow<NetworkInterfaceDetail?> = _currentNetworkInterfaceDetail

    //    是否在刷新中
    private val _refreshing = MutableStateFlow<Boolean>(false)
    val refreshing: StateFlow<Boolean> = _refreshing

    //    是否在捕获
    private val _capturing = MutableStateFlow<Boolean>(false)
    val capturing: StateFlow<Boolean> = _capturing

    val _selectedPacket = MutableStateFlow<PacketDetail?>(null)
    val selectedPacket: StateFlow<PacketDetail?> = _selectedPacket

    private val _selectedPacketFragment = MutableStateFlow<PacketFragment?>(null)
    val selectedPacketFragment: StateFlow<PacketFragment?> = _selectedPacketFragment

    val captureList = mutableStateListOf<PacketDetail>()

    private var _handle: PcapHandle? = null

    //    开始捕获的时间
    private var startCaptureTime: Long = -1

    private var no: Long = 1

    //    流量统计时间
    private var starStatisticalTime: Long = 0
    private var networkUpLoadTraffic: Long = 0//上传流量
    private var networkDownLoadTraffic: Long = 0//下载流量
    private var networkDownLoadPacket: Long = 0//下载包数量
    private var networkDownLoadTotalTraffic: Long = 0//下载总流量
    private var networkUpLoadTotalTraffic: Long = 0//上传总流量
    private var networkUploadPacket: Long = 0//上传包数量
    private var networkIcmpPacket: Long = 0//ICMP包数量
    private var networkArpPacket: Long = 0//Arp包数量
    private var networkTcpPacket: Long = 0//Tcp包数量
    private var networkUdpPacket: Long = 0//Udp包数量
    private var networkDnsPacket: Long = 0//Dns包数量


    private val _uploadTraffic = MutableStateFlow<Long>(0)
    val uploadTraffic: StateFlow<Long> = _uploadTraffic

    private val _downloadTraffic = MutableStateFlow<Long>(0)
    val downloadTraffic: StateFlow<Long> = _downloadTraffic

    private val _totalUploadTraffic = MutableStateFlow<Long>(0)
    val totalUploadTraffic: StateFlow<Long> = _totalUploadTraffic

    private val _totalDownloadTraffic = MutableStateFlow<Long>(0)
    val totalDownloadTraffic: StateFlow<Long> = _totalDownloadTraffic

     private val _totalTraffic = MutableStateFlow<Long>(0   )
         val totalTraffic: StateFlow<Long> = _totalTraffic


    private val _uploadPacket = MutableStateFlow<Long>(0)
    val uploadPacket: StateFlow<Long> = _uploadPacket

    private val _downloadPacket = MutableStateFlow<Long>(0)
    val downloadPacket: StateFlow<Long> = _downloadPacket

    private val _icmpPacket = MutableStateFlow<Long>(0)
    val icmpPacket: StateFlow<Long> = _icmpPacket

    private val _arpPacket = MutableStateFlow<Long>(0)
    val arpPacket: StateFlow<Long> = _arpPacket

    private val _tcpPacket = MutableStateFlow<Long>(0)
    val tcpPacket: StateFlow<Long> = _tcpPacket

    private val _udpPacket = MutableStateFlow<Long>(0)
    val udpPacket: StateFlow<Long> = _udpPacket

    private val _dnsPacket = MutableStateFlow<Long>(0)
    val dnsPacket: StateFlow<Long> = _dnsPacket

    var _currentNetworkMacAddress: MacAddress? = null

    //    刷新网络接口
    suspend fun refreshNetworkInterface() {
        withContext(Dispatchers.IO) {
            launch(Dispatchers.IO) {
                while (true) {
                    log("update coroutine")
                    println("refreshing...")
                    _refreshing.emit(true)
                    val runtime = Runtime.getRuntime()
//                从getmac指令中获取网卡的可读名称
//                Pcaps.findAllDevs()读取的太原始了
                    val commands = arrayOf("getmac", "/fo", "csv", "/v", "/nh")
                    val process = runtime.exec(commands)
                    val all = csvReader() {
//                        charset = "gb2312"
                    }.readAll(process.inputStream)
                    val list = mutableListOf<NetworkInterfaceMicrosoft>()
                    all.forEach {
                        list.add(NetworkInterfaceMicrosoft(it[0], it[1], it[2], it[3]))
                    }
//                部分的网卡已经断开链接了 .. 过滤掉
                    _networkInterfaces.emit(list.filter { it.name != "媒体已断开连接" })
                    _refreshing.emit(false)
                    println("refreshing complete")
                    delay(1000 * 5)
                }
            }
        }
    }

    suspend fun setNetworkInterface(networkInterfaceMicrosoft: NetworkInterfaceMicrosoft) {
        withContext(Dispatchers.IO) {
            val deviceName = networkInterfaceMicrosoft.name.replace("Tcpip", "NPF")
            val pcapNetworkInterface = Pcaps.getDevByName(deviceName)
//            设置当前的网卡的mac地址
            _currentNetworkMacAddress = MacAddress.getByName(networkInterfaceMicrosoft.macAddress)

            _currentNetworkInterface.emit(pcapNetworkInterface)
            _currentNetworkInterfaceDetail.emit(
                NetworkInterfaceDetail(
                    connectionName = networkInterfaceMicrosoft.connectionName,
                    realName = pcapNetworkInterface.name,
                    familyName = networkInterfaceMicrosoft.familyName,
                    address = pcapNetworkInterface.addresses
                )
            )
        }
    }

    fun setFilter(bpfString: String) {
        try {
            _handle?.setFilter(bpfString, BpfProgram.BpfCompileMode.NONOPTIMIZE).also {
                println("set filtering parameter:${bpfString}")
                captureList.clear()
                _selectedPacket.tryEmit(null)
                _selectedPacketFragment.tryEmit(null)
            }
        } catch (e: NotOpenException) {
            e.printStackTrace()
        } catch (e: PcapNativeException) {
            e.printStackTrace()
        }
    }

    //    开始捕获
    suspend fun startCapture() = withContext(Dispatchers.IO) {
        val version = Runtime::class.java.getPackage().implementationVersion
        println(version)
        launch {
            log("捕获协程")
            val pcapNetworkInterface = _currentNetworkInterface.value
            if (pcapNetworkInterface != null) {
                _handle = pcapNetworkInterface.openLive(65536, PcapNetworkInterface.PromiscuousMode.NONPROMISCUOUS, -1)
                if (_handle != null) {
                    _capturing.emit(true)
                    startCaptureTime = System.currentTimeMillis()
                    starStatisticalTime = System.currentTimeMillis()
                    try {
                        while (_capturing.value) {
                            val packet = _handle!!.nextPacket
                            if (packet != null) {

                                if (_handle!!.timestamp.time - starStatisticalTime >= 1000) {
//                                统计1S内的流量
                                    starStatisticalTime = _handle!!.timestamp.time
                                    _uploadTraffic.emit(networkUpLoadTraffic)
                                    _downloadTraffic.emit(networkDownLoadTraffic)
                                    _uploadPacket.emit(networkUploadPacket)
                                    _downloadPacket.emit(networkDownLoadPacket)
                                    _icmpPacket.emit(networkIcmpPacket)
                                    _arpPacket.emit(networkArpPacket)
                                    _tcpPacket.emit(networkTcpPacket)
                                    _udpPacket.emit(networkUdpPacket)
                                    _dnsPacket.emit(networkDnsPacket)
                                    _totalUploadTraffic.emit(networkUpLoadTotalTraffic)
                                    _totalDownloadTraffic.emit(networkDownLoadTotalTraffic)
                                    _totalTraffic.emit(networkUpLoadTotalTraffic + networkDownLoadTotalTraffic)
                                    networkUpLoadTraffic = 0
                                    networkDownLoadTraffic = 0
                                    networkUploadPacket = 0
                                    networkDownLoadPacket = 0

                                }


                                var type: PacketType? = null
                                if (packet.contains(TcpPacket::class.java)) {   //TCP报文
                                    networkTcpPacket++
                                    type = PacketType.TCP
                                } else if (packet.contains(DnsPacket::class.java)) { //DNS报文
                                    networkDnsPacket++
                                    type = PacketType.DNS
                                } else if (packet.contains(UdpPacket::class.java)) { //UDP报文
                                    networkUdpPacket++
                                    type = PacketType.UDP
                                } else if (packet.contains(IcmpV4CommonPacket::class.java)) { //ICMP报文
                                    networkIcmpPacket++
                                    type = PacketType.ICMP
                                } else if (packet.contains(ArpPacket::class.java)) { //Arp报文
                                    networkArpPacket++
                                    type = PacketType.ARP
                                }
                                if (type != null) {
                                    val length = packet.length()
//                                以太网
                                    if (_currentNetworkMacAddress == packet.get(EthernetPacket::class.java).header.dstAddr) {
//                                    下载流量

                                        networkDownLoadTraffic += length
                                        networkDownLoadTotalTraffic+=length
                                        networkDownLoadPacket++
                                    } else {
//                                    上传流量
                                        networkUpLoadTraffic += length
                                        networkUpLoadTotalTraffic += length
                                        networkUploadPacket++
                                    }


                                    if (type == PacketType.ARP) {
                                        val arpPacket = packet.get(ArpPacket::class.java)
                                        captureList.add(
                                            ArpPacketDetail(
                                                no++, _handle!!.timestamp.time, type, packet,
                                                arpPacket.header.srcHardwareAddr,
                                                arpPacket.header.dstHardwareAddr,
                                            )
                                        )
                                    } else {  //IP的地址就行
                                        if (packet.contains(IpV4Packet::class.java)) {
                                            val ipV4Packet = packet.get(IpV4Packet::class.java)
                                            captureList.add(
                                                IPv4PacketDetail(
                                                    no++, _handle!!.timestamp.time, type, packet,
                                                    ipV4Packet.header.srcAddr,
                                                    ipV4Packet.header.dstAddr,
                                                )
                                            )
                                        } else if (packet.contains(IpV4Packet::class.java)) {
                                            val ipV6Packet = packet.get(IpV6Packet::class.java)
                                            captureList.add(
                                                IPv6PacketDetail(
                                                    no++, _handle!!.timestamp.time, type, packet,
                                                    ipV6Packet.header.srcAddr,
                                                    ipV6Packet.header.srcAddr,
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }catch (e:NotOpenException){
                        println("shut down")
                    }
                }
            }
        }
    }


    //    停止捕获
    suspend fun stopCapture() = withContext(Dispatchers.IO) {
        _capturing.emit(false)
        _handle?.close()
    }

    suspend fun setSelectedPacket(packetDetail: PacketDetail) = withContext(Dispatchers.IO) {
        _highLight.emit(null)
        val packet = packetDetail.packet

        var ethernetFragment: MutableList<EthernetFragment> = mutableListOf()
        var ipv4Fragment: MutableList<Ipv4Fragment>? = null
        var arpFragment: MutableList<ArpFragment>? = null
        var tcpFragment: MutableList<TcpFragment>? = null
        var udpFragment: MutableList<UdpFragment>? = null
        var icmpFragment: MutableList<IcmpFragment>? = null

        var index = 0

//        以太网协议
        packet.containsAndDo(EthernetPacket::class.java) {
            ethernetFragment = mutableListOf()
            var ethernetIndex = index
            ethernetFragment.apply {
                add(EthernetFragment(ethernetIndex, 6, EthernetType.destination))
                ethernetIndex += 6
                add(EthernetFragment(ethernetIndex, 6, EthernetType.srouce))
                ethernetIndex += 6
                add(EthernetFragment(ethernetIndex, 2, EthernetType.type))
                ethernetIndex += 2
            }
            index += it.header.length()
        }

        packet.containsAndDo(ArpPacket::class.java) {
            arpFragment = mutableListOf()
            var arpIndex = index
            arpFragment!!.apply {
                add(ArpFragment(arpIndex, 2, ArpType.HardWareType))
                arpIndex += 2
                add(ArpFragment(arpIndex, 2, ArpType.ProtocolType))
                arpIndex += 2
                add(ArpFragment(arpIndex, 1, ArpType.HardWareAddrLength))
                arpIndex += 1
                add(ArpFragment(arpIndex, 1, ArpType.ProtocolAddrLength))
                arpIndex += 1
                add(ArpFragment(arpIndex, 2, ArpType.Operation))
                arpIndex += 2
                add(ArpFragment(arpIndex, 6, ArpType.SrcHardWareAddr))
                arpIndex += 6
                add(ArpFragment(arpIndex, 4, ArpType.SrcProtocolAddr))
                arpIndex += 4
                add(ArpFragment(arpIndex, 6, ArpType.DstHardWareAddr))
                arpIndex += 6
                add(ArpFragment(arpIndex, 4, ArpType.DstProtocolAddr))
                arpIndex += 4
            }
            index += it.header.length()
        }

        packet.containsAndDo(IpV4Packet::class.java) {
            ipv4Fragment = mutableListOf()
            var ipv4Index = index
            ipv4Fragment!!.apply {
                add(Ipv4Fragment(ipv4Index, 1, Ipv4Type.Version))
                add(Ipv4Fragment(ipv4Index, 1, Ipv4Type.HeaderLength))
                ipv4Index += 1
                add(Ipv4Fragment(ipv4Index, 1, Ipv4Type.DifferentialServicesField))
                ipv4Index += 1
                add(Ipv4Fragment(ipv4Index, 2, Ipv4Type.TotalLength))
                ipv4Index += 2
                add(Ipv4Fragment(ipv4Index, 2, Ipv4Type.Identification))
                ipv4Index += 2
                add(Ipv4Fragment(ipv4Index, 1, Ipv4Type.Flags))
                add(Ipv4Fragment(ipv4Index, 2, Ipv4Type.FragmentOffset))
                ipv4Index += 2
                add(Ipv4Fragment(ipv4Index, 1, Ipv4Type.TimeToLive))
                ipv4Index += 1
                add(Ipv4Fragment(ipv4Index, 1, Ipv4Type.Protocol))
                ipv4Index += 1
                add(Ipv4Fragment(ipv4Index, 2, Ipv4Type.HeaderCheckSum))
                ipv4Index += 2
                add(Ipv4Fragment(ipv4Index, 4, Ipv4Type.SourceAddress))
                ipv4Index += 4
                add(Ipv4Fragment(ipv4Index, 4, Ipv4Type.DestinationAddress))
                ipv4Index += 4
            }

            index += it.header.length()
        }

        packet.containsAndDo(IcmpV4CommonPacket::class.java) {
            icmpFragment = mutableListOf()
            var icmpIndex = index
            icmpFragment!!.apply {
                add(IcmpFragment(icmpIndex, 1, IcmpType.Type))
                icmpIndex += 1
                add(IcmpFragment(icmpIndex, 1, IcmpType.Code))
                icmpIndex += 1
                add(IcmpFragment(icmpIndex, 2, IcmpType.Checksum))
                icmpIndex += 2

                it.containsAndDo(IcmpV4EchoPacket::class.java) {
                    add(IcmpFragment(icmpIndex, 2, IcmpType.Identifier))
                    icmpIndex += 2
                    add(IcmpFragment(icmpIndex, 2, IcmpType.SeqNumber))
                    icmpIndex += 2
                    add(IcmpFragment(icmpIndex, it.payload.length(), IcmpType.Payload))
                }

                it.containsAndDo(IcmpV4EchoReplyPacket::class.java) {
                    add(IcmpFragment(icmpIndex, 2, IcmpType.Identifier))
                    icmpIndex += 2
                    add(IcmpFragment(icmpIndex, 2, IcmpType.SeqNumber))
                    icmpIndex += 2
                    add(IcmpFragment(icmpIndex, it.payload.length(), IcmpType.Payload))
                }
                it.containsAndDo(IcmpV4DestinationUnreachablePacket::class.java) {
                    add(IcmpFragment(icmpIndex, 2, IcmpType.Unused))
                    icmpIndex += 2
                    add(IcmpFragment(icmpIndex, it.payload.length(), IcmpType.Payload))
                }

            }


        }

        packet.containsAndDo(TcpPacket::class.java) {
            tcpFragment = mutableListOf()
            var tcpIndex = index
            tcpFragment!!.apply {
                add(TcpFragment(tcpIndex, 2, TcpType.SourcePort))
                tcpIndex += 2
                add(TcpFragment(tcpIndex, 2, TcpType.DestinationPort))
                tcpIndex += 2
                add(TcpFragment(tcpIndex, 4, TcpType.SeqNumber))
                tcpIndex += 4
                add(TcpFragment(tcpIndex, 4, TcpType.AckNumber))
                tcpIndex += 4
                add(TcpFragment(tcpIndex, 1, TcpType.DataOffset))
                add(TcpFragment(tcpIndex, 2, TcpType.Flags))
                tcpIndex += 2
                add(TcpFragment(tcpIndex, 2, TcpType.Window))
                tcpIndex += 2
                add(TcpFragment(tcpIndex, 2, TcpType.Checksum))
                tcpIndex += 2
                add(TcpFragment(tcpIndex, 2, TcpType.UrgentPointer))
                tcpIndex += 2
                val optionLength = it.header.options.sumBy { it.length() }
                add(TcpFragment(tcpIndex, optionLength, TcpType.Options))
                tcpIndex += optionLength
                add(TcpFragment(tcpIndex, it.payload?.length() ?: 0, TcpType.Payload))
            }
            index += it.header.length()
        }

        packet.containsAndDo(UdpPacket::class.java) {
            udpFragment = mutableListOf()
            var udpIndex = index
            udpFragment!!.apply {
                add(UdpFragment(udpIndex, 2, UdpType.SourcePort))
                udpIndex += 2
                add(UdpFragment(udpIndex, 2, UdpType.DestinationPort))
                udpIndex += 2
                add(UdpFragment(udpIndex, 2, UdpType.Length))
                udpIndex += 2
                add(UdpFragment(udpIndex, 2, UdpType.Checksum))
                udpIndex += 2
                add(UdpFragment(udpIndex, it.payload?.length() ?: 0, UdpType.Payload))
            }
            index += it.header.length()

        }


        _selectedPacketFragment.emit(
            PacketFragment(
                ethernetFragment, packet, ipv4Fragment, arpFragment, tcpFragment, udpFragment, icmpFragment
            )
        )
        _selectedPacket.emit(packetDetail)

    }


    private val _highLight = MutableStateFlow<HighLight?>(null)
    val highLight: StateFlow<HighLight?> = _highLight
    suspend fun ethernetFragmentHighLight(ethernetType: EthernetType) = withContext(Dispatchers.IO) {
        if (selectedPacketFragment.value == null)
            return@withContext
        val packetFragment = selectedPacketFragment!!.value!!
        packetFragment.ethernetFragment.forEach {
            if (it.type == ethernetType) {
//                高亮的部分区域
                _highLight.emit(HighLight(it.start, it.length))
                return@withContext
            }
        }
    }

    suspend fun arpFragmentHighLight(arpType: ArpType) = withContext(Dispatchers.IO) {
        if (selectedPacketFragment.value == null)
            return@withContext
        val packetFragment = selectedPacketFragment!!.value!!
        packetFragment.arpFragment?.forEach {
            if (it.type == arpType) {
//                高亮的部分区域
                _highLight.emit(HighLight(it.start, it.length))
                return@withContext
            }
        }
    }

    suspend fun ipv4FragmentHighLight(ipv4Type: Ipv4Type) = withContext(Dispatchers.IO) {
        if (selectedPacketFragment.value == null)
            return@withContext
        val packetFragment = selectedPacketFragment!!.value!!
        packetFragment.ipv4Fragment?.forEach {
            if (it.type == ipv4Type) {
//                高亮的部分区域
                _highLight.emit(HighLight(it.start, it.length))
                return@withContext
            }
        }
    }

    suspend fun tcpFragmentHighLight(tcpType: TcpType) = withContext(Dispatchers.IO) {
        if (selectedPacketFragment.value == null)
            return@withContext
        val packetFragment = selectedPacketFragment!!.value!!
        packetFragment.tcpFragment?.forEach {
            if (it.type == tcpType) {
//                高亮的部分区域
                _highLight.emit(HighLight(it.start, it.length))
                return@withContext
            }
        }
    }

    suspend fun udpFragmentHighLight(udpType: UdpType) = withContext(Dispatchers.IO) {
        if (selectedPacketFragment.value == null)
            return@withContext
        val packetFragment = selectedPacketFragment!!.value!!
        packetFragment.udpFragment?.forEach {
            if (it.type == udpType) {
//                高亮的部分区域
                _highLight.emit(HighLight(it.start, it.length))
                return@withContext
            }
        }
    }

    suspend fun icmpFragmentHighLight(icmpType: IcmpType) = withContext(Dispatchers.IO) {
        if (selectedPacketFragment.value == null)
            return@withContext
        val packetFragment = selectedPacketFragment!!.value!!
        packetFragment.icmpFragment?.forEach {
            if (it.type == icmpType) {
//                高亮的部分区域
                _highLight.emit(HighLight(it.start, it.length))
                return@withContext
            }
        }
    }
}

//高亮
data class HighLight(val start: Int, val length: Int)

fun <T : Packet> Packet.containsAndDo(clazz: Class<T>, content: (packet: T) -> Unit) {
    if (contains(clazz)) {
        content(get(clazz))
    }
}

@Composable
fun <T : Packet> Packet.containsAndDoCompose(clazz: Class<T>, content: (@Composable (packet: T) -> Unit)) {
    if (contains(clazz)) {
        content(get(clazz))
    }
}


data class PacketFragment(
    val ethernetFragment: List<EthernetFragment>,
    val packet: Packet,
    val ipv4Fragment: List<Ipv4Fragment>? = null,
    val arpFragment: List<ArpFragment>? = null,
    val tcpFragment: List<TcpFragment>? = null,
    val udpFragment: List<UdpFragment>? = null,
    val icmpFragment: List<IcmpFragment>? = null,
)


abstract class PacketDetail {
    abstract val no: Long //数据包编号
    abstract val time: Long //到达的时间
    abstract val type: PacketType //协议类型
    abstract val packet: Packet
}

data class IPv4PacketDetail(
    override val no: Long,
    override val time: Long,
    override val type: PacketType,
    override val packet: Packet,
    val source: Inet4Address, //源地址IP
    val destination: Inet4Address, //目的地址IP
) : PacketDetail()

data class IPv6PacketDetail(
    override val no: Long,
    override val time: Long,
    override val type: PacketType,
    override val packet: Packet,
    val source: Inet6Address, //源地址IP
    val destination: Inet6Address, //目的地址IP
) : PacketDetail()

data class ArpPacketDetail(
    override val no: Long,
    override val time: Long,
    override val type: PacketType,
    override val packet: Packet,
    val source: MacAddress, //源地址MAC
    val destination: MacAddress, //目的MAC地址
) : PacketDetail()


data class NetworkInterfaceMicrosoft(
    val connectionName: String, //链接名称 本地连接* 4
    val familyName: String,//网络适配器  Microsoft Wi-Fi
    val macAddress: String, //mac地址66-5D-86-2B-8C-ED
    val name: String //传输名称  \Device\Tcpip_{00C0410B-CB6C-48FE-86B6-C0CC27D90FD2}
)

data class NetworkInterfaceDetail(
    val connectionName: String, //链接名称 本地连接* 4
    val realName: String, //网卡物理名称
    val familyName: String,//网络适配器  Microsoft Wi-Fi
    val address: List<PcapAddress>
)


object viewModel {
    val pcap = PcapViewModel()
    val packetBuilder = PacketViewModel()
}


sealed class PacketType(val name: String) {
    object TCP : PacketType("TCP")
    object UDP : PacketType("UDP")
    object DNS : PacketType("DNS")  //基于UDP
    object ARP : PacketType("ARP")  //地址解析协议
    object ICMP : PacketType("ICMP")  //ping
    object HTTP : PacketType("HTTP")
}


fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")

enum class EthernetType {
    destination,
    srouce,
    type
}

data class EthernetFragment(
    val start: Int,    //  [start , start+length )
    val length: Int,
    val type: EthernetType,
)

enum class Ipv4Type {
    Version,
    HeaderLength,
    DifferentialServicesField,
    TotalLength,
    Identification,
    Flags,
    FragmentOffset,
    TimeToLive,
    Protocol,
    HeaderCheckSum,
    SourceAddress,
    DestinationAddress,
}

data class Ipv4Fragment(
    val start: Int,    //  [start , start+length )
    val length: Int,
    val type: Ipv4Type,
)

enum class ArpType {
    HardWareType,
    ProtocolType,
    HardWareAddrLength,
    ProtocolAddrLength,
    Operation,
    SrcHardWareAddr,
    SrcProtocolAddr,
    DstHardWareAddr,
    DstProtocolAddr,
}

data class ArpFragment(
    val start: Int,    //  [start , start+length )
    val length: Int,
    val type: ArpType,
)

enum class TcpType {
    SourcePort,
    DestinationPort,
    SeqNumber,
    AckNumber,
    DataOffset,
    Flags,
    Window,
    Checksum,
    UrgentPointer,
    Options,
    Payload
}

enum class UdpType {
    SourcePort,
    DestinationPort,
    Length,
    Checksum,
    Payload
}

enum class IcmpType {
    Type,
    Code,
    Checksum,
    Identifier,
    SeqNumber,
    Unused,
    Payload
}

data class IcmpFragment(
    val start: Int,    //  [start , start+length )
    val length: Int,
    val type: IcmpType
)

data class TcpFragment(
    val start: Int,    //  [start , start+length )
    val length: Int,
    val type: TcpType
)

data class UdpFragment(
    val start: Int,    //  [start , start+length )
    val length: Int,
    val type: UdpType
)