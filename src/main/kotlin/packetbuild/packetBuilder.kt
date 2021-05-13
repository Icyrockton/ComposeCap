package packetbuild

import androidx.compose.animation.Animatable
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 *@Author : Icyrockton
 *@Date: 2021/5/12 9:24
 **/
//协议构造
@Composable
fun PacketBuilder() {
    var route by remember {
        mutableStateOf(PacketRoute.EthernetPacket.route)
    }
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().height(80.dp).padding(start = 10.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            packetRoutes.forEach {
                PacketRouteItem(route, it) {
                    route = it
                }
            }
        }
//        内容
        Card (modifier = Modifier.fillMaxWidth().weight(1.0f).padding(end = 20.dp,bottom = 20.dp)) {
            Crossfade(route) { packetScreen ->
                when (packetScreen) {
                    PacketRoute.EthernetPacket.route -> Ethernet()
                    PacketRoute.Ipv4Packet.route -> Ipv4()
                    PacketRoute.TcpPacket.route -> Tcp()
                    PacketRoute.UdpPacket.route -> Udp()
                    PacketRoute.Build.route -> PacketBuild()
                }
            }
        }
    }
}

@Composable
fun PacketRouteItem(currentRoute: String, route: PacketRoute, onClick: (route: String) -> Unit) {
    val bgColor = remember { Animatable(Color(0xFF6200EE)) }
    val textColor = remember { Animatable(Color.White) }
    LaunchedEffect(currentRoute) {
        if (route.route == currentRoute) {
            bgColor.animateTo(Color.White, tween(300,easing = LinearEasing))
        } else {
            bgColor.animateTo(Color(0xFF6200EE), tween(300,easing = LinearEasing))
        }
    }
    LaunchedEffect(currentRoute){
        if (route.route == currentRoute) {
            textColor.animateTo(Color(0xFF6200EE), tween(300,easing = LinearEasing))
        } else {
            textColor.animateTo(Color.White, tween(300,easing = LinearEasing))
        }
    }
    Surface(
        modifier = Modifier.width(200.dp).height(50.dp).padding(start = 10.dp).clickable { onClick(route.route) },
        color = bgColor.value,
        shape = RoundedCornerShape(10.dp, 10.dp),
        elevation = 10.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = route.description,color = textColor.value)
        }
    }
}

val packetRoutes =
    listOf(PacketRoute.EthernetPacket, PacketRoute.Ipv4Packet, PacketRoute.TcpPacket, PacketRoute.UdpPacket,PacketRoute.Build)

sealed class PacketRoute(val route: String, val description: String) {
    object EthernetPacket : PacketRoute("ethernet", "以太帧部分")
    object Ipv4Packet : PacketRoute("ipv4", "IP报文部分")
    object TcpPacket : PacketRoute("tcp", "TCP报文部分")
    object UdpPacket : PacketRoute("udp", "UDP报文部分")
    object Build : PacketRoute("build","构造报文")
}