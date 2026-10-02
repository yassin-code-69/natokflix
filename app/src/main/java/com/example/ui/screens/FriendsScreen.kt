package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FriendProfile
import com.example.data.model.FriendStatus
import com.example.data.model.User
import com.example.ui.components.GenderAvatar
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.NatokGold
import com.example.ui.theme.NatokRed
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun FriendsScreen(
    user: User,
    friends: List<FriendProfile>,
    onOpenChat: (FriendProfile) -> Unit,
    onSendFriendRequest: (String) -> Unit,
    onAcceptFriendRequest: (String) -> Unit,
    onRejectFriendRequest: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // 0: চ্যাট ইনবক্স (Inbox), 1: বন্ধু খুঁজুন (Discover), 2: ফ্রেন্ড রিকোয়েস্ট (Requests), 3: আমার ফ্রেন্ডস (My Friends)
    var selectedSubTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredFriends = remember(friends, searchQuery) {
        if (searchQuery.isBlank()) friends
        else friends.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.location.contains(searchQuery, ignoreCase = true) ||
            it.bio.contains(searchQuery, ignoreCase = true)
        }
    }

    val activeChats = remember(filteredFriends) {
        filteredFriends.filter { it.status == FriendStatus.ACCEPTED || it.lastMessage.isNotBlank() }
    }

    val discoverUsers = remember(filteredFriends) {
        filteredFriends.filter { it.status == FriendStatus.NONE || it.status == FriendStatus.REQUEST_SENT }
    }

    val friendRequests = remember(filteredFriends) {
        filteredFriends.filter { it.status == FriendStatus.REQUEST_RECEIVED }
    }

    val myFriendsList = remember(filteredFriends) {
        filteredFriends.filter { it.status == FriendStatus.ACCEPTED }
    }

    val tabTitles = listOf(
        "চ্যাট ইনবক্স (${activeChats.size})",
        "বন্ধু খুঁজুন",
        "রিকোয়েস্ট (${friendRequests.size})",
        "আমার ফ্রেন্ডস (${myFriendsList.size})"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .testTag("friends_screen_container")
    ) {
        // Top Header Banner
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "আড্ডা ও চ্যাট",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .background(NatokGold.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                            .border(0.5.dp, NatokGold, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "LIVE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = NatokGold
                        )
                    }
                }

                // Friends counter pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color(0xFF2B2038), RoundedCornerShape(14.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = null,
                        tint = NatokGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "ফ্রেন্ডস: ${user.friendsCount}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("নাম বা জেলা লিখে ফ্রেন্ড খুঁজুন...", fontSize = 12.sp, color = TextSecondary) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = TextSecondary, modifier = Modifier.size(18.dp))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("friend_search_input"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NatokRed,
                    unfocusedBorderColor = BorderDark,
                    focusedContainerColor = Color(0xFF16121E),
                    unfocusedContainerColor = Color(0xFF16121E),
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Strict Media Policy Alert Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E1724), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = NatokGold,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "টেক্সট-অনলি চ্যাট: ডাটা সাশ্রয়ে ছবি/অডিও/ভিডিও ফাইল পাঠানো নিষিদ্ধ।",
                    fontSize = 10.sp,
                    color = Color(0xFFC0BACB),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Sub-tabs row
        ScrollableTabRow(
            selectedTabIndex = selectedSubTab,
            containerColor = Color(0xFF1F1A2B),
            contentColor = Color.White,
            edgePadding = 12.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedSubTab]),
                    color = NatokRed,
                    height = 3.dp
                )
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedSubTab == index,
                    onClick = { selectedSubTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (selectedSubTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedSubTab == index) Color.White else TextSecondary
                        )
                    },
                    modifier = Modifier.testTag("subtab_friend_$index")
                )
            }
        }

        // Tab Content
        AnimatedContent(
            targetState = selectedSubTab,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "friends_tab_anim"
        ) { tabIndex ->
            when (tabIndex) {
                0 -> {
                    // ==========================================
                    // 1. ACTIVE CHATS / INBOX
                    // ==========================================
                    if (activeChats.isEmpty()) {
                        EmptyFriendsPlaceholder(
                            title = "কোনো সক্রিয় চ্যাট নেই",
                            subtitle = "‘বন্ধু খুঁজুন’ অপশন থেকে ফ্রেন্ড বানিয়ে মেসেজ আদান-প্রদান করুন।"
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(activeChats, key = { it.id }) { friend ->
                                ActiveChatCard(
                                    friend = friend,
                                    onClick = { onOpenChat(friend) }
                                )
                            }
                        }
                    }
                }

                1 -> {
                    // ==========================================
                    // 2. DISCOVER / FIND FRIENDS
                    // ==========================================
                    if (discoverUsers.isEmpty()) {
                        EmptyFriendsPlaceholder(
                            title = "কোনো নতুন ইউজার পাওয়া যায়নি",
                            subtitle = "অন্য নাম বা এলাকা দিয়ে অনুসন্ধান করুন।"
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(discoverUsers, key = { it.id }) { friend ->
                                DiscoverUserCard(
                                    friend = friend,
                                    onSendRequest = { onSendFriendRequest(friend.id) },
                                    onOpenChat = { onOpenChat(friend) }
                                )
                            }
                        }
                    }
                }

                2 -> {
                    // ==========================================
                    // 3. FRIEND REQUESTS
                    // ==========================================
                    if (friendRequests.isEmpty()) {
                        EmptyFriendsPlaceholder(
                            title = "কোনো পেন্ডিং রিকোয়েস্ট নেই",
                            subtitle = "কেউ ফ্রেন্ড রিকোয়েস্ট পাঠালে এখানে প্রদর্শিত হবে।"
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(friendRequests, key = { it.id }) { friend ->
                                FriendRequestCard(
                                    friend = friend,
                                    onAccept = { onAcceptFriendRequest(friend.id) },
                                    onReject = { onRejectFriendRequest(friend.id) }
                                )
                            }
                        }
                    }
                }

                3 -> {
                    // ==========================================
                    // 4. MY FRIENDS
                    // ==========================================
                    if (myFriendsList.isEmpty()) {
                        EmptyFriendsPlaceholder(
                            title = "আপনার কোনো ফ্রেন্ড যুক্ত নেই",
                            subtitle = "‘বন্ধু খুঁজুন’ ট্যাব থেকে রিকোয়েস্ট পাঠিয়ে ফ্রেন্ডলিস্ট বড় করুন।"
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(myFriendsList, key = { it.id }) { friend ->
                                MyFriendItemCard(
                                    friend = friend,
                                    onOpenChat = { onOpenChat(friend) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActiveChatCard(
    friend: FriendProfile,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("chat_card_${friend.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GenderAvatar(
                gender = friend.gender,
                name = friend.name,
                size = 48.dp,
                showOnlineDot = true,
                isOnline = friend.isOnline,
                isBot = friend.isBot
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = friend.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (friend.isBot) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("⭐", fontSize = 10.sp)
                        }
                    }
                    Text(
                        text = friend.lastMessageTime.ifBlank { "এখনই" },
                        fontSize = 10.sp,
                        color = Color(0xFF888899)
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = friend.lastMessage.ifBlank { "চ্যাট শুরু করতে ক্লিক করুন..." },
                    fontSize = 12.sp,
                    color = if (friend.unreadCount > 0) NatokGold else Color(0xFFB0A4C0),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun DiscoverUserCard(
    friend: FriendProfile,
    onSendRequest: () -> Unit,
    onOpenChat: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("discover_card_${friend.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GenderAvatar(
                    gender = friend.gender,
                    name = friend.name,
                    size = 52.dp,
                    showOnlineDot = true,
                    isOnline = friend.isOnline,
                    isBot = friend.isBot
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = friend.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        if (friend.isBot) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .background(NatokGold.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "বট ফ্রেন্ড",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NatokGold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "📍 ${friend.location} • বয়স: ${friend.age} বছর",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    if (friend.bio.isNotBlank()) {
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = friend.bio,
                            fontSize = 11.sp,
                            color = Color(0xFFCCCCCC),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (friend.status == FriendStatus.REQUEST_SENT) {
                    OutlinedButton(
                        onClick = {},
                        enabled = false,
                        modifier = Modifier.weight(1f).height(40.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            disabledContentColor = Color(0xFFAAAAAA)
                        )
                    ) {
                        Icon(Icons.Default.HourglassTop, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("রিকোয়েস্ট পাঠানো হয়েছে", fontSize = 11.sp)
                    }
                } else {
                    Button(
                        onClick = onSendRequest,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("send_req_${friend.id}"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NatokRed)
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ফ্রেন্ড রিকোয়েস্ট পাঠান", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                OutlinedButton(
                    onClick = onOpenChat,
                    modifier = Modifier.height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(16.dp), tint = NatokGold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("মেসেজ", fontSize = 12.sp, color = NatokGold)
                }
            }
        }
    }
}

@Composable
fun FriendRequestCard(
    friend: FriendProfile,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("req_card_${friend.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GenderAvatar(
                    gender = friend.gender,
                    name = friend.name,
                    size = 48.dp,
                    isBot = friend.isBot
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = friend.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "📍 ${friend.location} • আপনাকে ফ্রেন্ড রিকোয়েস্ট পাঠিয়েছেন",
                        fontSize = 11.sp,
                        color = NatokGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onAccept,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("accept_req_${friend.id}"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("গ্রহণ করুন (Accept)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("reject_req_${friend.id}"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE57373))
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("বাতিল (Delete)", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun MyFriendItemCard(
    friend: FriendProfile,
    onOpenChat: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("my_friend_${friend.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GenderAvatar(
                gender = friend.gender,
                name = friend.name,
                size = 44.dp,
                showOnlineDot = true,
                isOnline = friend.isOnline,
                isBot = friend.isBot
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = friend.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    if (friend.isBot) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("💖", fontSize = 11.sp)
                    }
                }
                Text(
                    text = "📍 ${friend.location}",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            Button(
                onClick = onOpenChat,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NatokRed),
                modifier = Modifier.height(36.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("মেসেজ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun EmptyFriendsPlaceholder(
    title: String,
    subtitle: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF231B30)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = null,
                    tint = NatokGold,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 18.sp
            )
        }
    }
}
