package packetbuild

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.pcap4j.packet.*
import org.pcap4j.packet.namednumber.*
import org.pcap4j.util.Inet4NetworkAddress
import org.pcap4j.util.MacAddress
import java.net.Inet4Address

/**
 *@Author : Icyrockton
 *@Date: 2021/5/12 13:13
 **/
class PacketViewModel {

    private val _ethernetPacket = MutableStateFlow<EthernetBuilderPacket>(
        EthernetBuilderPacket(
            destinationAddress = "64:5d:86:2b:8c:ed",
            sourceAddress = "d8:c8:e9:fc:f9:b1",
            type = "800"
        )
    )
    val ethernetPacket: StateFlow<EthernetBuilderPacket> = _ethernetPacket

    private val _ipv4Packet = MutableStateFlow<Ipv4BuilderPacket>(
        Ipv4BuilderPacket(
            version = "4",
            headerLength = "5",
            totalLength = "87",
            identification = "7981",
            dontFragmentFlag = "false",
            moreFragmentFlag = "false",
            fragmentOffset = "0",
            headerChecksum = "0000",
            srcAddr = "192.168.123.179",
            dstAddr = "195.154.179.2",
            ttl = "128",
            protocol = "17",
        )
    )
    val ipv4Packet: StateFlow<Ipv4BuilderPacket> = _ipv4Packet

    private val _tcpPacket = MutableStateFlow<TcpBuilderPacket>(
        TcpBuilderPacket(
            sourcePort = "443",
            destinationPort = "6595",
            seqNumber = "885246",
            ackNumber = "886706",
            headerLength = "5",
            urgFlag = "false",
            ackFlag = "false",
            pushFlag = "false",
            resetFlag = "false",
            synFlag = "false",
            finFlag = "true",
            window = "2640",
            checkSum = "6742",
            urgentPointer = "0"
        )
    )
    val tcpPacket: StateFlow<TcpBuilderPacket> = _tcpPacket



    private val _udpPacket = MutableStateFlow<UdpBuilderPacket>(
        UdpBuilderPacket(
            sourcePort = "49875",
            destinationPort = "6881",
            length = "57",
            checkSum = "0"
        )
    )
    val udpPacket: StateFlow<UdpBuilderPacket> = _udpPacket

    private val _packet = MutableStateFlow<Packet?>(null)
    val packet: StateFlow<Packet?> = _packet

    fun updateEthernetPacket(newPacket: EthernetBuilderPacket) {
        _ethernetPacket.tryEmit(newPacket)
    }

    fun updateIpv4Packet(newPacket: Ipv4BuilderPacket) {
        _ipv4Packet.tryEmit(newPacket)
    }

    fun updateTcpPacket(newPacket: TcpBuilderPacket) {
        _tcpPacket.tryEmit(newPacket)
    }

    fun updateUdpPacket(udpPacket: UdpBuilderPacket) {
        _udpPacket.tryEmit(udpPacket)
    }

    fun buildEthernetPacket() {
        _ethernetPacket.value.also {
            val packet = EthernetPacket.Builder().apply {
                srcAddr(MacAddress.getByName(it.sourceAddress, ":"))
                dstAddr(MacAddress.getByName(it.destinationAddress, ":"))
                type(EtherType.getInstance(it.type.toShort(16)))
                paddingAtBuild(true)
            }.build()
            _packet.tryEmit(packet)
            println(packet)
        }
    }

    fun buildIpPacket() {
        val ipv4Packet = IpV4Packet.Builder()
        _ipv4Packet.value.also {
            ipv4Packet.apply {
                version(IpVersion.getInstance(it.version.toByte()))
                ihl(it.headerLength.toByte())
                tos(IpV4Rfc1349Tos.newInstance(0))
                totalLength(it.totalLength.toShort())
                identification(it.identification.toShort(16))
                dontFragmentFlag(it.dontFragmentFlag.toBoolean())
                moreFragmentFlag(it.moreFragmentFlag.toBoolean())
                fragmentOffset(it.fragmentOffset.toShort())
                ttl(it.ttl.toShort().toByte())
                protocol(IpNumber.getInstance(it.protocol.toByte()))
                headerChecksum(it.headerChecksum.toShort())
                srcAddr(Inet4Address.getByName(it.srcAddr) as Inet4Address)
                dstAddr(Inet4Address.getByName(it.dstAddr) as Inet4Address)
            }
        }

        val etherNetPacket = EthernetPacket.Builder()

        _ethernetPacket.value.also {
            val packet = etherNetPacket.apply {
                srcAddr(MacAddress.getByName(it.sourceAddress, ":"))
                dstAddr(MacAddress.getByName(it.destinationAddress, ":"))
                type(EtherType.getInstance(it.type.toShort(16)))
                payloadBuilder(ipv4Packet)
                paddingAtBuild(true)
            }.build()
            _packet.tryEmit(packet)


//            IP报文创建
        }
    }

    fun buildTcpPacket() {

        val tcpPacket = TcpPacket.Builder()
        _tcpPacket.value.also {
            tcpPacket.apply {
                srcPort(TcpPort.getInstance(it.sourcePort.toShort()))
                dstPort(TcpPort.getInstance(it.destinationPort.toShort()))
                sequenceNumber(it.seqNumber.toInt())
                acknowledgmentNumber(it.ackNumber.toInt())
                dataOffset(it.headerLength.toByte())
                reserved(0)
                urg(it.urgFlag.toBoolean())
                ack(it.ackFlag.toBoolean())
                psh(it.pushFlag.toBoolean())
                rst(it.resetFlag.toBoolean())
                syn(it.synFlag.toBoolean())
                fin(it.finFlag.toBoolean())
                window(it.window.toShort())
                checksum(it.checkSum.toShort())
                urgentPointer(it.urgentPointer.toShort())
            }
        }


        val ipv4Packet = IpV4Packet.Builder()
        _ipv4Packet.value.also {
            ipv4Packet.apply {
                version(IpVersion.getInstance(it.version.toByte()))
                ihl(it.headerLength.toByte())
                tos(IpV4Rfc1349Tos.newInstance(0))
                totalLength(it.totalLength.toShort())
                identification(it.identification.toShort(16))
                dontFragmentFlag(it.dontFragmentFlag.toBoolean())
                moreFragmentFlag(it.moreFragmentFlag.toBoolean())
                fragmentOffset(it.fragmentOffset.toShort())
                ttl(it.ttl.toShort().toByte())
                protocol(IpNumber.getInstance(it.protocol.toByte()))
                headerChecksum(it.headerChecksum.toShort())
                srcAddr(Inet4Address.getByName(it.srcAddr) as Inet4Address)
                dstAddr(Inet4Address.getByName(it.dstAddr) as Inet4Address)
                payloadBuilder(tcpPacket)
            }
        }

        val etherNetPacket = EthernetPacket.Builder()

        _ethernetPacket.value.also {
            val packet = etherNetPacket.apply {
                srcAddr(MacAddress.getByName(it.sourceAddress, ":"))
                dstAddr(MacAddress.getByName(it.destinationAddress, ":"))
                type(EtherType.getInstance(it.type.toShort(16)))
                payloadBuilder(ipv4Packet)
                paddingAtBuild(true)
            }.build()
//            IP报文创建
            _packet.tryEmit(packet)

        }
    }

    fun buildUdpPacket() {


        val udpPacket = UdpPacket.Builder()
        _udpPacket.value.also {
            udpPacket.apply {
                srcPort(UdpPort.getInstance(it.sourcePort.toInt().toShort()))
                dstPort(UdpPort.getInstance(it.destinationPort.toInt().toShort()))
                length(it.length.toShort())
                checksum(it.checkSum.toShort())
            }
        }

        val ipv4Packet = IpV4Packet.Builder()
        _ipv4Packet.value.also {
            ipv4Packet.apply {
                version(IpVersion.getInstance(it.version.toByte()))
                ihl(it.headerLength.toByte())
                tos(IpV4Rfc1349Tos.newInstance(0))
                totalLength(it.totalLength.toShort())
                identification(it.identification.toShort(16))
                dontFragmentFlag(it.dontFragmentFlag.toBoolean())
                moreFragmentFlag(it.moreFragmentFlag.toBoolean())
                fragmentOffset(it.fragmentOffset.toShort())
                ttl(it.ttl.toShort().toByte())
                protocol(IpNumber.getInstance(it.protocol.toByte()))
                headerChecksum(it.headerChecksum.toShort())
                srcAddr(Inet4Address.getByName(it.srcAddr) as Inet4Address)
                dstAddr(Inet4Address.getByName(it.dstAddr) as Inet4Address)
                payloadBuilder(udpPacket)
            }
        }

        val etherNetPacket = EthernetPacket.Builder()

        _ethernetPacket.value.also {
            val packet = etherNetPacket.apply {
                srcAddr(MacAddress.getByName(it.sourceAddress, ":"))
                dstAddr(MacAddress.getByName(it.destinationAddress, ":"))
                type(EtherType.getInstance(it.type.toShort(16)))
                payloadBuilder(ipv4Packet)
                paddingAtBuild(true)
            }.build()
            _packet.tryEmit(packet)

            println(packet)
//            IP报文创建

        }
    }
}