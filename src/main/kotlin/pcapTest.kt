import androidx.compose.runtime.collectAsState
import com.sun.jna.FunctionMapper
import com.sun.jna.Native
import com.sun.jna.NativeLibrary
import com.sun.jna.Structure
import com.sun.jna.ptr.PointerByReference
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.*
import org.pcap4j.core.*
import org.pcap4j.packet.IpV4Packet
import org.pcap4j.packet.Packet
import org.pcap4j.util.NifSelector
import java.lang.reflect.Method
import java.net.InetAddress

/**
 *@Author : Icyrockton
 *@Date: 2021/5/7 14:28
 **/


fun HuangAo() = flow<String> {

}

fun main() = runBlocking<Unit> {
    val pcapNetworkInterface = Pcaps.getDevByAddress(InetAddress.getByName("192.168.123.179"))
    val pcapHandle = pcapNetworkInterface.openLive(65536, PcapNetworkInterface.PromiscuousMode.NONPROMISCUOUS, -1)
    pcapHandle.setFilter("icmp",BpfProgram.BpfCompileMode.OPTIMIZE)

    while (true){

        val packet = pcapHandle.nextPacket
        if (packet!=null){
            println(packet)
        }
    }
}



