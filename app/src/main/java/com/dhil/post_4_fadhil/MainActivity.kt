package com.dhil.post_4_fadhil

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.dhil.post_4_fadhil.databinding.ActivityMainBinding
import kotlin.text.insert

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var db: DatabaseWarga
    private lateinit var dao: WargaDao
    private lateinit var executor: AppExecutor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        executor = AppExecutor()
        db = DatabaseWarga.getDatabase(this)
        dao = db.wargaDao()

        val rg = binding.rgJenisKelamin
        val spinner = binding.spStatus

        dao.getAllWarga().observe(this, Observer { list ->
            if (list.isEmpty()) {
                binding.tvListWarga.text = "Belum ada data warga yang tersimpan."
            } else {
                val builder = StringBuilder()
                list.forEachIndexed { index, warga ->
                    builder.append("👤 Warga ${index + 1}\n")
                    builder.append("Nama: ${warga.nama}\n")
                    builder.append("NIK: ${warga.nik}\n")
                    builder.append("Kabupaten: ${warga.kabupaten}\n")
                    builder.append("Kecamatan: ${warga.kecamatan}\n")
                    builder.append("Desa: ${warga.desa}\n")
                    builder.append("RT/RW: ${warga.rt}/${warga.rw}\n")
                    builder.append("Jenis Kelamin: ${warga.jenisKelamin}\n")
                    builder.append("Status Pernikahan: ${warga.statusPernikahan}\n")
                    builder.append("------------------------------------\n")
                }
                binding.tvListWarga.text = builder.toString()
            }
        })

        binding.btnSimpan.setOnClickListener {
            val nama = binding.etNamaLengkap.text.toString()
            val nik = binding.etNIK.text.toString()
            val kab = binding.etKabupaten.text.toString()
            val kec = binding.etKecamatan.text.toString()
            val desa = binding.etDesa.text.toString()
            val rt = binding.etRT.text.toString()
            val rw = binding.etRW.text.toString()
            val jenisKelamin =
                if (rg.checkedRadioButtonId == binding.rbLaki.id) "Laki-Laki" else "Perempuan"
            val status = spinner.selectedItem.toString()

            if (nama.isBlank() || nik.isBlank()) {
                Toast.makeText(this, "Nama dan NIK wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val wargaBaru = Warga(
                nama = nama,
                nik = nik,
                kabupaten = kab,
                kecamatan = kec,
                desa = desa,
                rt = rt,
                rw = rw,
                jenisKelamin = jenisKelamin,
                statusPernikahan = status
            )

            executor.diskIO.execute {
                dao.insert(wargaBaru)
            }

            Toast.makeText(this, "Data warga berhasil disimpan", Toast.LENGTH_SHORT).show()
            resetForm()
        }

        binding.btnReset.setOnClickListener {
            resetForm()
            executor.diskIO.execute {
                dao.deleteAll()
            }
            Toast.makeText(this, "Semua data warga telah dihapus", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resetForm() {
        binding.etNamaLengkap.text.clear()
        binding.etNIK.text.clear()
        binding.etKabupaten.text.clear()
        binding.etKecamatan.text.clear()
        binding.etDesa.text.clear()
        binding.etRT.text.clear()
        binding.etRW.text.clear()
        binding.rgJenisKelamin.clearCheck()
        binding.spStatus.setSelection(0)
    }
}