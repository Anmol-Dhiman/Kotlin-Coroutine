package com.example.coroutine

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis
import kotlin.time.measureTime

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val TAG = "MainActivity"

//        here we have to coroutine
//        here the globalScope means that this coroutine will alive until we have our app alive
//        and this coroutine can run on any of the thread which is available
//        it will dies after it's job is done
        GlobalScope.launch {
//            here dealy is the suspend function and we can only use
//            these type of function inside the coroutine or other suspend function
            delay(5000L)
            Log.d(TAG, "hello from coroutine form the thread ${Thread.currentThread().name}")
        }
        Log.d(TAG, "hello from main thread ${Thread.currentThread().name}")


//        here this coroutine help us to learn the context swithcing part
//        the Dispactechers are used tell about the thread or the context in which the coroutine have to work
//        here the io means the input output operation which we have to do in the other thread
        GlobalScope.launch(Dispatchers.IO) {
            Log.d(TAG, "hello from the IO opration ${Thread.currentThread().name}")
            val answer = networkCallSampel1()
            withContext(Dispatchers.Main) {
                Log.d(TAG, "hello from the main threa ${Thread.currentThread().name}")

            }
        }

//        this runBlocking is used to block the main thread
//Thread.sleep() can do the exact same work but if we want to use our custom suspend methods then
//        runBlocking  is the most usefull coroutine
//we can use other coroutine inside the block coroutine and they will run asyncronously

        runBlocking {
            delay(5000L)
        }


//        here the coroutine return the job when we execute them

        val job = GlobalScope.launch(Dispatchers.Default) {

//            here the withTimeout() function will terminate the process which takes the time more than the 3sec

            withTimeout(3000L) {
//                here we will code process which we have to run
            }
        }

        runBlocking {
//            to cancel the job we have to inform the coroutine that we are going to cancel it
//            and for that the coroutine should have the dealy or some time boundation so that we can inform the coroutine
//            while we are using the withTimeout() so it will automatically termiante the process so for that we don't need the runblocking
            job.cancel()
        }

//            if we want to do two or more network calls at the same time then we have to use the async and await instead of launch because they will run sequencly
//            and also if we want that our coroutine return something then we have to use the async instead of launch

        GlobalScope.launch {
            val time = measureTimeMillis {
                val answer1 = async { networkCallSampel1() }
                val answer2 = async { networkCallSampel2() }
                Log.d(TAG, "the output of network call is ${answer1.await()}")
                Log.d(TAG, "the output of network call is ${answer2.await()}")
            }
            Log.d(TAG, "time taken for the async $time .ms")
        }


//        using GlobalScope is the bad practice as it leads to the garabge collectoin and the memory leak problems
//because if we define the GlobalScope coroutine in a activity and it uses the resources of that activity and now we are moved to the other activity and the previous activity
//        is finished then also the GlobalScope exists and can use the resources of the previous activity
//        because the GlobalScope is exists till our app exists in the memory


//        to counter that issue we use the lifecycleScope

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            lifecycleScope.launch {
                while (true) {
                    delay(1000L)
                    Log.d(TAG, "main Activity ")
                }
            }
            GlobalScope.launch {
                delay(5000L)
                Intent(this@MainActivity, SampleActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
            }
        }

    }


    suspend fun networkCallSampel1(): String {
        delay(3000L)
        return "The Result1"
    }

    suspend fun networkCallSampel2(): String {
        delay(3000L)
        return "The Result2"
    }
}