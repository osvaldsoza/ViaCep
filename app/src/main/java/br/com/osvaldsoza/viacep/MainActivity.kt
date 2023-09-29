package br.com.osvaldsoza.viacep

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.osvaldsoza.viacep.api.Api
import br.com.osvaldsoza.viacep.databinding.ActivityMainBinding
import br.com.osvaldsoza.viacep.model.Endereco
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        const val URL = "https://viacep.com.br/"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        window.statusBarColor = Color.parseColor("#FF018786")
        val actionBar = supportActionBar
        actionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FF018786")))

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(URL)
            .build()
            .create(Api::class.java)

        binding.btnBuscarCep.setOnClickListener {
            val cep = binding.edtCep.text.toString()

            if (cep.isEmpty()) {
                Toast.makeText(this, "Preencha o cep!", Toast.LENGTH_SHORT).show()
            } else {
                retrofit.setEndereco(cep).enqueue(object : Callback<Endereco> {
                    override fun onResponse(call: Call<Endereco>, response: Response<Endereco>) {
                        if (response.code() == 200) {
                            val logradouro = response.body()?.logradouro.toString()
                            val localidade = response.body()?.localidade.toString()
                            val bairro = response.body()?.bairro.toString()
                            val uf = response.body()?.uf.toString()
                            setFormulario(logradouro, localidade, bairro, uf)
                        } else {
                            Toast.makeText(applicationContext, "Cep inválido!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Endereco>, t: Throwable) {
                        Toast.makeText(applicationContext, "Erro inesperado.", Toast.LENGTH_SHORT).show()
                    }

                })
            }
        }
    }

    private fun setFormulario(logradouro: String, localidade: String, bairro: String, uf: String) {
        binding.edtLogradouro.setText(logradouro)
        binding.edtCidade.setText(localidade)
        binding.edtBairro.setText(bairro)
        binding.edtEstado.setText(uf)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var inflate = menuInflater
        inflate.inflate(R.menu.menu_principal,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.reset ->{
                binding.edtLogradouro.setText("")
                binding.edtCidade.setText("")
                binding.edtBairro.setText("")
                binding.edtEstado.setText("")
                binding.edtCep.setText("")
            }
        }
        return super.onOptionsItemSelected(item)
    }
}