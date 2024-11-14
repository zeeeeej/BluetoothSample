package com.yunnext.bluetooth.sample.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import color
import com.yunnext.bluetooth.sample.repo.ble.currentTime
import com.yunnext.bluetooth.sample.ui.common.CommitButton
import com.yunnext.bluetooth.sample.ui.common.CommonArrowTitle
import com.yunnext.bluetooth.sample.ui.common.CommonEditText
import kotlinx.coroutines.delay
import randomZhongGuoSe

enum class Type {
    Server, Client;
}

@Composable
fun SPPComponent(
    modifier: Modifier = Modifier,
    debugShow: Boolean = false,
    typeOwner: TypeOwner,
    onSelected: (Type) -> Unit,
    onSend: (String) -> Unit,
    onStartServer: () -> Unit,
    onOpenBlueList: () -> Unit,
) {
    var show by remember { mutableStateOf(debugShow) }
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        CommonArrowTitle(modifier = Modifier .clickable {
            show = !show
        },title = "SPP蓝牙串口协议", opened = show)
        AnimatedVisibility(show) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (typeOwner) {


                    TypeOwner.NaN -> {


                        Text("选择角色", style = TextStyle.Default.copy(color = Color(0xff999999)))
                        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                            Text(
                                Type.Server.name,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(
                                        1.dp,
                                        randomZhongGuoSe().color,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .clickable { onSelected.invoke(Type.Server) })
                            Text(
                                Type.Client.name,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(
                                        1.dp,
                                        randomZhongGuoSe().color,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .clickable { onSelected.invoke(Type.Client) })
                        }


                    }

                    is TypeOwner.Type -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("当前角色：${typeOwner.type.name}", modifier = Modifier
                                .clickable {
                                    onStartServer()
                                }
                            )

                            Text(
                                if (typeOwner.connected) when (typeOwner.type) {
                                    Type.Server -> "停止服务"
                                    Type.Client -> "断开连接"
                                } else when (typeOwner.type) {
                                    Type.Server -> "开启服务"
                                    Type.Client -> "打开设备列表，选择要连接的设备"
                                },
                                style = TextStyle.Default.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier
                                    .padding(16.dp)
                                    .clickable(
                                        enabled = when (typeOwner.type) {
                                            Type.Server -> true
                                            Type.Client -> true
                                        }
                                    ) {
                                        when (typeOwner.type) {
                                            Type.Server -> onStartServer()
                                            Type.Client -> onOpenBlueList()
                                        }
                                    }
                            )

                            Text(
                                typeOwner.connectedInfo,
                                style = TextStyle.Default.copy(fontSize = 11.sp),
                                modifier = Modifier
                                    .clickable {
                                        onStartServer()
                                    }
                            )
                        }


                        var msg: String by remember {
                            mutableStateOf("")
                        }
                        Text(
                            when (typeOwner) {
                                TypeOwner.NaN -> "NaN"
                                else -> {
                                    "对方：" + typeOwner.connectAddress
                                }
                            },
                            style = TextStyle.Default.copy(fontSize = 11.sp)
                        )
                        MsgList(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(.5.dp, color = randomZhongGuoSe().color),
                            msgList = typeOwner.msgList,
                            type = typeOwner.type
                        )
                        Text(
                            when (typeOwner) {
                                TypeOwner.NaN -> "NaN"
                                else -> {
                                    "自己"
                                }
                            },
                            style = TextStyle.Default.copy(
                                fontSize = 11.sp,
                                textAlign = TextAlign.End
                            ),
                            modifier = Modifier.align(Alignment.End)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CommonEditText(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                value = msg,
                                hint = "请输入消息内容"
                            ) {
                                msg = it
                            }

                            CommitButton(
                                modifier = Modifier,
                                text = "发送",
                                enable = msg.isNotEmpty()
                            ) {
                                onSend(msg)
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            var start: Boolean by remember(typeOwner.start) { mutableStateOf(typeOwner.start) }
                            var direction: Direction? by remember(typeOwner.direction) { mutableStateOf(typeOwner.direction













































                            ) }
                            ControllerComponent(modifier = Modifier.size(120.dp), onCommand = { c ->
                                when (c) {
                                    Command.Up -> {
                                        direction = Direction.Up
                                        onSend(Command.Up.cmd)
                                    }

                                    Command.Down -> {
                                        direction = Direction.Down
                                        onSend(Command.Down.cmd)
                                    }

                                    Command.Left -> {
                                        direction = Direction.Left
                                        onSend(Command.Left.cmd)
                                    }

                                    Command.Right -> {
                                        direction = Direction.Right
                                        onSend(Command.Right.cmd)
                                    }

                                    Command.Start -> {
                                        start = true
                                        direction = null
                                        onSend(Command.Start.cmd)
                                    }

                                    Command.Stop -> {
                                        start = false
                                        direction = null
                                        onSend(Command.Stop.cmd)
                                    }

                                    Command.OpenDiscovery -> {
                                        TODO()
                                    }
                                }
                            }, start = start, direction = direction)

//                        Row(
//                            verticalAlignment = Alignment.CenterVertically,
//                            horizontalArrangement = Arrangement.spacedBy(12.dp)
//                        ) {
//
//                            CommandComponent(command = Command.Up, on = true, onClick = {
//                                onSend(Command.Up.cmd)
//                            })
//                            CommandComponent(command = Command.Down, on = true, onClick = {
//                                onSend(Command.Down.cmd)
//                            })
//                            CommandComponent(command = Command.Left, on = true, onClick = {
//                                onSend(Command.Left.cmd)
//                            })
//
//                            CommandComponent(command = Command.Right, on = true, onClick = {
//                                onSend(Command.Right.cmd)
//                            })
//
//                            CommandComponent(command = Command.Start, on = true, onClick = {
//                                onSend(Command.Start.cmd)
//                            })
//
//                            CommandComponent(command = Command.Stop, on = true, onClick = {
//                                onSend(Command.Stop.cmd)
//                            })
//
                            CommandComponent(
                                command = Command.OpenDiscovery,
                                on = true,
                                onClick = {
                                    when (typeOwner.type) {
                                        Type.Server -> {

                                        }

                                        Type.Client -> {
                                            onSend(Command.OpenDiscovery.cmd)
                                        }
                                    }

                                })
//
//                        }
                        }


                    }
                }
            }
        }

    }
}


data class Msg(val type: Type, val msg: String, val timestamp: Long = currentTime())

@Composable
private fun MsgList(modifier: Modifier = Modifier, msgList: List<Msg>, type: Type) {
    val rememberLazyListState = rememberLazyListState()
    LaunchedEffect(msgList.size) {
        delay(10)
        rememberLazyListState.scrollToItem(0)
    }
    LazyRow(
        modifier = modifier.heightIn(min = 56.dp),
        state = rememberLazyListState,
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(msgList, { it.toString() }) {
            MsgItem(msg = it, type = type)
        }
    }
}

@Composable
private fun MsgItem(modifier: Modifier = Modifier, msg: Msg, type: Type) {
    Box(modifier.height(56.dp)) {
        Text(
            msg.msg,
            style = TextStyle.Default.copy(fontSize = 12.sp),
            modifier =

            Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White.copy(if (msg.type != type) .5f else 1f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .align(
                    when {
                        msg.type != type -> {
                            Alignment.TopStart
                        }

                        else -> {
                            Alignment.BottomEnd
                        }
                    }
                ),
        )
    }
}

@Preview("SDPComponentPreviewClient")
@Composable
fun SPPComponentPreviewClient() {
    SPPComponent(
        modifier = Modifier.fillMaxWidth(),
        typeOwner = TypeOwner.Type(type = Type.Client, connectAddress = "", connected = false, start = false, openDiscovery = false, direction = null),
        onSelected = {},
        onSend = {},
        onStartServer = {}, onOpenBlueList = {})
}

@Preview("SDPComponentPreviewServer")
@Composable
fun SPPComponentPreviewServer() {
    SPPComponent(
        modifier = Modifier.fillMaxWidth(),
        debugShow = true,
        typeOwner = TypeOwner.Type(type = Type.Server, connectAddress = "", connected = false, start = false, openDiscovery = false, direction = null),
        onSelected = {},
        onSend = {},
        onStartServer = {}, onOpenBlueList = {})
}


@Preview("Client", backgroundColor = 0xffff0000)
@Composable
fun MsgListPreviewClient(modifier: Modifier = Modifier) {
    MsgList(modifier = Modifier.fillMaxWidth(), msgList = (1..10).map {
        val type = Type.values().random()
        Msg(type = type, msg = "来自${type.name}的消息：msg-$it")
    }, type = Type.Client)
}

@Preview("Server", backgroundColor = 0xffff0000)

@Composable
fun MsgListPreviewServer(modifier: Modifier = Modifier) {
    MsgList(modifier = Modifier.fillMaxWidth(), msgList = (1..10).map {
        val type = Type.values().random()
        Msg(type = type, msg = "来自${type.name}的消息：msg-$it")
    }, type = Type.Server)
}

enum class Command {
    Up, Down, Left, Right, Start, Stop, OpenDiscovery
    ;
}

val Command.cmd: String
    get() = "Command:" + this.name


val Command.display: String
    get() = when (this) {
        Command.Up -> "上"
        Command.Down -> "下"
        Command.Left -> "左"
        Command.Right -> "右"
        Command.Start -> "启动"
        Command.Stop -> "停止"
        Command.OpenDiscovery -> "打开对外广播服务"
    }


@Composable
private fun CommandComponent(
    modifier: Modifier = Modifier,
    command: Command,
    on: Boolean,
    onClick: () -> Unit
) {
    Text(
        command.display, style = TextStyle.Default.copy(color = Color.White, fontSize = 11.sp),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black)
            .clickable { onClick() }
            .padding(12.dp)
            .alpha(if (on) 1f else .25f)
    )
}