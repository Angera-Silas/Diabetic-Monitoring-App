package com.example.diab
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class ChatActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var messageList: MutableList<Message>
    private lateinit var adapter: MessagesAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageButton

    private var senderId: String? = null
    private var receiverId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Initialize views
        recyclerView = findViewById(R.id.messagesRecyclerView)
        messageInput = findViewById(R.id.messageEditText)
        sendButton = findViewById(R.id.sendButton)

        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        // Get senderId and receiverId from intent extras
        senderId = intent.getStringExtra("senderId")
        receiverId = intent.getStringExtra("receiverId")

        if (senderId == null || receiverId == null) {
            Toast.makeText(this, "Sender or receiver ID not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize message list and adapter
        messageList = mutableListOf()
        adapter = MessagesAdapter(this, messageList, senderId!!)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Load messages from Firestore
        loadMessages()

        // Set click listener for send button
        sendButton.setOnClickListener {
            sendMessage()
        }
    }

    private fun loadMessages() {
        // Query messages where senderId and receiverId match in both directions
        db.collection("chats")
            .document("$senderId-$receiverId")
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Toast.makeText(
                        this,
                        "Failed to load messages: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@addSnapshotListener
                }

                messageList.clear()
                if (snapshot != null) {
                    for (doc in snapshot.documents) {
                        val message = doc.toObject(Message::class.java)
                        if (message != null) {
                            messageList.add(message)
                        }
                    }
                    adapter.notifyDataSetChanged()
                    recyclerView.scrollToPosition(messageList.size - 1)
                }
            }

        // Also, query messages where receiverId and senderId match to cover both directions
        db.collection("chats")
            .document("$receiverId-$senderId")
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Toast.makeText(
                        this,
                        "Failed to load messages: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    for (doc in snapshot.documents) {
                        val message = doc.toObject(Message::class.java)
                        if (message != null && !messageList.contains(message)) { // Ensure no duplicates
                            messageList.add(message)
                        }
                    }
                    // Sort messages by timestamp again in case new messages were added
                    messageList.sortBy { it.timestamp }
                    adapter.notifyDataSetChanged()
                    recyclerView.scrollToPosition(messageList.size - 1)
                }
            }
    }

    private fun sendMessage() {
        val messageText = messageInput.text.toString().trim()
        if (messageText.isNotEmpty()) {
            val message = Message(senderId!!, receiverId!!, messageText, System.currentTimeMillis())

            // Determine sender type
            val senderType = determineSenderType(senderId!!)

            // Add message to sender's collection
            db.collection("chats")
                .document("$senderId-$receiverId")
                .collection("messages")
                .add(message)
                .addOnSuccessListener {
                    messageInput.text.clear()
                    recyclerView.scrollToPosition(messageList.size - 1)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to send message: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }

            // Add message to receiver's collection
            db.collection("chats")
                .document("$receiverId-$senderId")
                .collection("messages")
                .add(message)
                .addOnSuccessListener {
                    // No need to clear messageInput or scroll here
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to send message: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

    private fun determineSenderType(senderId: String) {
        val userType = when {
            senderId.startsWith("P", ignoreCase = true) -> {
                "Patient"
            }
            senderId.startsWith("D", ignoreCase = true) -> {
                "Doctor"
            }
            else -> {
                "Unknown"
            }
        }

        // Show toast message based on userType
        Toast.makeText(this, "User type: $userType", Toast.LENGTH_SHORT).show()
    }

}