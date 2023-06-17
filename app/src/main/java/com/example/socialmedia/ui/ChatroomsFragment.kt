package com.example.socialmedia.ui

import android.app.AlertDialog
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmedia.R
import com.example.socialmedia.adapters.ChatroomsAdapter
import com.example.socialmedia.models.Chatroom
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class ChatroomsFragment : Fragment() {

    private lateinit var chatroomsRV: RecyclerView
    private lateinit var createChatroomFAB: FloatingActionButton

    private var chatroomsAdapter: ChatroomsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chatrooms, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatroomsRV = view.findViewById(R.id.chatrooms_rv)
        setUpRecyclerView()

        createChatroomFAB = view.findViewById(R.id.create_chatroom)
        setUpCreateChatroom()
    }

    private fun setUpRecyclerView() {
        val firestore = FirebaseFirestore.getInstance()
        val query = firestore.collection("Chatrooms").orderBy("name")

        val firestoreRecyclerOptions = FirestoreRecyclerOptions.Builder<Chatroom>()
            .setQuery(query, Chatroom::class.java).build()

        chatroomsAdapter = activity?.let {
            ChatroomsAdapter(firestoreRecyclerOptions, it)
        }

        chatroomsRV.adapter = chatroomsAdapter
        chatroomsRV.layoutManager = LinearLayoutManager(activity)
    }

    private fun setUpCreateChatroom() {
        createChatroomFAB.setOnClickListener {
            val alertDialog: AlertDialog.Builder = AlertDialog.Builder(context)

            val editText = EditText(context)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // width
                LinearLayout.LayoutParams.WRAP_CONTENT // height
            )

            params.marginStart = dpToPixels(8f).toInt()
            params.marginEnd = dpToPixels(8f).toInt()

            editText.layoutParams = params
            editText.setPadding(0, dpToPixels(20f).toInt(), 0, dpToPixels(20f).toInt())

            alertDialog.setTitle("Create Chatroom")
            alertDialog.setMessage("Enter the name of the new chatroom that you want to create:")

            alertDialog.setView(editText)


            var textEntered = ""

            /*alertDialog.setPositiveButton("Create", object: DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {

                }
            })*/

            alertDialog.setPositiveButton("Create") { _, _ ->
                textEntered = editText.text.toString()
                val document = FirebaseFirestore.getInstance().collection("Chatrooms").document()
                val chatroom = Chatroom(document.id, textEntered)
                document.set(chatroom)
            }

            alertDialog.setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }

            alertDialog.show()
        }
    }

    private fun dpToPixels(dpValue: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dpValue,
            resources.displayMetrics
        )
    }

    override fun onResume() {
        super.onResume()
        chatroomsAdapter?.startListening()
    }

    override fun onPause() {
        super.onPause()
        chatroomsAdapter?.stopListening()
    }

}