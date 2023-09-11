package ru.karasevm.privatednstoggle

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuBinderWrapper
import rikka.shizuku.ShizukuProvider
import rikka.shizuku.SystemServiceHelper
import ru.karasevm.privatednstoggle.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), AddServerDialogFragment.NoticeDialogListener, DeleteServerDialogFragment.NoticeDialogListener, Shizuku.OnRequestPermissionResultListener {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var binding: ActivityMainBinding
    private var items = mutableListOf<String>()
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var adapter: RecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Shizuku.addRequestPermissionResultListener(this::onRequestPermissionResult)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        linearLayoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = linearLayoutManager

        sharedPrefs = this.getSharedPreferences("app_prefs", 0)

        items = sharedPrefs.getString("dns_servers", "")!!.split(",").toMutableList()
        if (items[0] == "") {
            items.removeAt(0)
        }
        adapter = RecyclerAdapter(items)
        adapter.onItemClick = { position ->
            val newFragment = DeleteServerDialogFragment(position)
            newFragment.show(supportFragmentManager, "delete_server")
        }
        binding.recyclerView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onResume() {
        super.onResume()
        // Check if Shizuku is available
        if (Shizuku.pingBinder()) {
            // check if permission is granted already
            val isGranted = if (Shizuku.isPreV11() || Shizuku.getVersion() < 11) {
                checkSelfPermission(ShizukuProvider.PERMISSION) == PackageManager.PERMISSION_GRANTED
            } else {
                Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
            }
            // request permission if not granted
            if (!isGranted && !Shizuku.shouldShowRequestPermissionRationale()) {
                if (Shizuku.isPreV11() || Shizuku.getVersion() < 11) {
                    requestPermissions(arrayOf(ShizukuProvider.PERMISSION), 1)
                } else {
                    Shizuku.requestPermission(1)
                }
            }
        } else {
            if (checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://karasevm.github.io/PrivateDNSAndroid/"))
                startActivity(browserIntent)
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Shizuku.removeRequestPermissionResultListener(this::onRequestPermissionResult)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.add_server -> {
            val newFragment = AddServerDialogFragment()
            newFragment.show(supportFragmentManager, "add_server")
            true
        }
        R.id.privacy_policy -> {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://karasevm.github.io/PrivateDNSAndroid/privacy_policy"))
            startActivity(browserIntent)
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    override fun onDialogPositiveClick(dialog: DialogFragment, server: String) {
        if (server.isEmpty()) {
            Toast.makeText(this, R.string.server_length_error, Toast.LENGTH_SHORT).show()
            return
        }
        items.add(server)
        adapter.setData(items.toMutableList())
        binding.recyclerView.adapter?.notifyItemInserted(items.size - 1)
        sharedPrefs.edit()
            .putString("dns_servers", items.joinToString(separator = ",") { it }).apply()
    }

    override fun onDialogPositiveClick(dialog: DialogFragment,position: Int) {
        items.removeAt(position)
        adapter.setData(items.toMutableList())
        adapter.notifyItemRemoved(position)
        sharedPrefs.edit()
            .putString("dns_servers", items.joinToString(separator = ",") { it }).apply()

    }

    @SuppressLint("PrivateApi")
    override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
        val isGranted = grantResult == PackageManager.PERMISSION_GRANTED

        if (isGranted) {
            val packageName = "ru.karasevm.privatednstoggle"

            val iPmClass = Class.forName("android.content.pm.IPackageManager")
            val iPmStub = Class.forName("android.content.pm.IPackageManager\$Stub")
            val asInterfaceMethod = iPmStub.getMethod("asInterface", IBinder::class.java)
            val grantRuntimePermissionMethod = iPmClass.getMethod(
                "grantRuntimePermission",
                String::class.java /* package name */,
                String::class.java /* permission name */,
                Int::class.java /* user ID */
            )

            val iPmInstance = asInterfaceMethod.invoke(
                null, ShizukuBinderWrapper(
                    SystemServiceHelper.getSystemService("package")
                )
            )

            grantRuntimePermissionMethod.invoke(
                iPmInstance,
                packageName,
                Manifest.permission.WRITE_SECURE_SETTINGS,
                0
            )
        } else if (checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://karasevm.github.io/PrivateDNSAndroid/"))
            startActivity(browserIntent)
            finish()
        }

    }

}