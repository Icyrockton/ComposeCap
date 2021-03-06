import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.*
import androidx.compose.desktop.Window
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.svgResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import data.viewModel
import kotlinx.coroutines.launch
import org.pcap4j.core.Pcaps
import packetbuild.PacketBuilder
import traffic.NetWorkTraffic

fun main() = Window(title = "ComposeCap", size = IntSize(1400, 1200), resizable = false) {

    var route by remember { mutableStateOf(NavigationItem.Capture.route) }
    MaterialTheme {
        Row(modifier = Modifier.fillMaxSize()) {
            NavigationBar(onClick = { route = it })
            Box(modifier = Modifier.weight(1.0f).fillMaxHeight().background(Color(0xFFEFEFEF))) {
                Crossfade(targetState = route) { screen ->
                    when (screen) {
                        NavigationItem.Capture.route -> Capture()
                        NavigationItem.Fee.route -> Fee()
                        NavigationItem.PacketBuilder.route -> PacketBuilder()
                        NavigationItem.Traffic.route -> NetWorkTraffic()
                    }
                }
            }
        }
    }
}


@Composable
fun NavigationBar(onClick: (String) -> Unit) {
    Box(
        modifier = Modifier.width(320.dp).fillMaxHeight().background(Color(0xFFEFEFEF))
    ) {
        Surface(
            modifier = Modifier.fillMaxSize().padding(end = 40.dp),
            color = MaterialTheme.colors.primary,
            elevation = 20.dp
        ) {
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
//                ??????
                Text(
                    "ComposeCap",
                    style = MaterialTheme.typography.h4,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 20.dp)
                )
                Text("??????????????????", style = MaterialTheme.typography.h4, textAlign = TextAlign.Center)
//                ????????????
                Column(
                    modifier = Modifier.fillMaxWidth().weight(1.0f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    routes.forEachIndexed { index, route ->
                        RouteButton(route.description) {
                            onClick(route.route)
                        }
                    }
                }
                NetworkInterface()
            }
        }
    }
}

@Composable
fun RouteButton(title: String, onClick: () -> Unit) {
    OutlinedButton(onClick = onClick, modifier = Modifier.width(240.dp).height(60.dp).padding(top = 20.dp)) {
        Text(text = title)
    }
}

@Composable
fun NetworkInterface() {
    //                ??????????????????
    val networkInterfaces by viewModel.pcap.networkInterfaces.collectAsState()
    val currentNetworkInterfaceDetail by viewModel.pcap.currentNetworkInterfaceDetail.collectAsState()
    val refreshing by viewModel.pcap.refreshing.collectAsState()
    val capturing by viewModel.pcap.capturing.collectAsState()
    val infiniteTransition = rememberInfiniteTransition()
//    ????????????
    val rotate by infiniteTransition.animateFloat(
        0f,
        360f,
        InfiniteRepeatableSpec(animation = tween(1000, easing = LinearEasing))
    )
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(true) {
        coroutineScope.launch {
            viewModel.pcap.refreshNetworkInterface()
        }
    }

    Column(modifier = Modifier.fillMaxWidth().height(400.dp)) {
        Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).padding(horizontal = 10.dp).background(Color.White))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {

            var expanded by remember { mutableStateOf(false) }
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.width(240.dp).height(60.dp).padding(top = 20.dp)
            ) {
                Text(text = "????????????")
            }

            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                networkInterfaces.forEachIndexed { index, networkInterface ->
                    key(networkInterface.name) {
                        if (index != 0)
                            Spacer(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp).height(1.dp)
                                    .background(Color.Gray)
                            )
                        DropdownMenuItem({
                            coroutineScope.launch {
                                viewModel.pcap.setNetworkInterface(networkInterface)
                            }
//                            ??????DropdownMenu
                            expanded = false
                        }) {
                            Text(text = "${networkInterface.connectionName}\n${networkInterface.familyName}")
                        }
                    }
                }
            }
        }
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp).padding(top = 20.dp).weight(1.0f)) {
            if (refreshing) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(svgResource("svg/refresh.svg"), null, modifier = Modifier.size(30.dp).rotate(rotate))
                    Text(text = "???????????????...")
                }
            }
            if (currentNetworkInterfaceDetail == null) {
                Text(
                    text = "??????????????????",
                    style = MaterialTheme.typography.subtitle1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxSize().padding(top = 20.dp)
                )
            } else if (currentNetworkInterfaceDetail != null) {

                Text(text = "????????????", style = MaterialTheme.typography.subtitle1)
                Text(text = currentNetworkInterfaceDetail!!.connectionName, style = MaterialTheme.typography.caption)
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "??????????????????", style = MaterialTheme.typography.subtitle1)
                Text(text = currentNetworkInterfaceDetail!!.realName, style = MaterialTheme.typography.caption)
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "????????????", style = MaterialTheme.typography.subtitle1)
                Text(text = currentNetworkInterfaceDetail!!.familyName, style = MaterialTheme.typography.caption)
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "IPv4/IPv6??????", style = MaterialTheme.typography.subtitle1)
                currentNetworkInterfaceDetail!!.address.forEach {
                    Text(text = Pcaps.toBpfString(it.address), style = MaterialTheme.typography.caption)
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp), horizontalArrangement = Arrangement.Center) {
            OutlinedButton(
                onClick = {
                    if (capturing){
                        coroutineScope.launch {
                            viewModel.pcap.stopCapture()
                        }
                    }
                    else{
                        coroutineScope.launch {
                            viewModel.pcap.startCapture()
                        }
                    }
                },
                modifier = Modifier.width(240.dp).height(40.dp)
            ) {
                if (capturing){
                    Text(text = "????????????")
                }
                else{
                    Text(text = "????????????")
                }
            }
        }
    }
}


val routes = listOf<NavigationItem>(NavigationItem.Capture,NavigationItem.PacketBuilder,NavigationItem.Traffic, NavigationItem.Fee)

sealed class NavigationItem(val route: String, val description: String) {
    object Capture : NavigationItem("capture", "????????????")
    object PacketBuilder : NavigationItem("packetBuilder", "????????????")
    object Fee : NavigationItem("fee", "????????????")
    object Traffic : NavigationItem("traffic", "????????????")
}

