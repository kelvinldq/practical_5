package my.marcusneo.practical_5

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import my.marcusneo.practical_5.databinding.ActivitySecondBinding

class SecondActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecondBinding
    //private var counter: Int = 0
    private val counterViewModel: CounterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //binding.tvCounter.text = counter.toString()
        //binding.tvCounter.text = counterViewModel._counter.toString()

        counterViewModel._counter.observe(this, Observer {
            binding.tvCounter.text = counterViewModel._counter.value.toString()
        })

        binding.btnPlus.setOnClickListener {
            //counter += 1
            //binding.tvCounter.text = counter.toString()
            counterViewModel.increment()
            binding.tvCounter.text = counterViewModel._counter.value.toString()
        }

        binding.btnMinus.setOnClickListener {
            //counter -= 1
            //binding.tvCounter.text = counter.toString()
            counterViewModel.decrement()
            binding.tvCounter.text = counterViewModel._counter.value.toString()
        }

        binding.btnClose.setOnClickListener {
            finish()
        }

    }
}