package com.app.hotspringsofbc

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.hotspringsofbc.models.Place
import com.app.hotspringsofbc.models.UserMap
import com.google.android.gms.ads.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.*


const val EXTRA_USER_MAP = "EXTRA_USER_MAP"
const val EXTRA_MAP_TITLE = "EXTRA_MAP_TITLE"
private const val FILENAME = "UserMaps.data"
private const val REQUEST_CODE = 1234
private const val TAG = "MainActivity"
private lateinit var rvMaps: RecyclerView
lateinit var mAdView : AdView


class MainActivity : AppCompatActivity() {


    private lateinit var adRequest: AdRequest
    private lateinit var userMaps: MutableList<UserMap>
    private lateinit var mapAdapter: MapsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rvMaps = findViewById(R.id.rvMaps)
        MobileAds.initialize(this) {}



        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        mAdView.adListener = object: AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            override fun onAdFailedToLoad(adError : LoadAdError) {
                // Code to be executed when an ad request fails.
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                Toast.makeText(this@MainActivity, "Thank You!",Toast.LENGTH_SHORT).show()

            }
        }


        //Interstitial ads

        userMaps = deserializeUserMaps(this).toMutableList()
        // Set layout manager on the recycler view
        rvMaps.layoutManager = LinearLayoutManager(this)
        // Set adapter on the recycler view
        mapAdapter = MapsAdapter(this, userMaps, object: MapsAdapter.OnClickListener {
            override fun onItemClick(position: Int) {
                Log.i(TAG, "onItemClick $position")
                Log.d(TAG, "onItemClick $position")
                // When user taps on view in RV, navigate to new activity
                if (position == 0) {
                    val intent1 = Intent(this@MainActivity, WebViewActivity::class.java)
                    startActivity(intent1)
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                }
                else {
                    val intent2 = Intent(this@MainActivity, DisplayMapActivity::class.java)
                    intent2.putExtra(EXTRA_USER_MAP, userMaps[position])
                    startActivity(intent2)
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                }
            }
        })
        rvMaps.adapter =  mapAdapter

        val floatingActionButton : FloatingActionButton = findViewById(R.id.floatingActionButton)
        floatingActionButton.setOnClickListener {
            Log.i(TAG,"Tap on FAB")
            showAlertDialog()
        }

    }

    private fun showAlertDialog() {
        val mapFormView = LayoutInflater.from(this).inflate(R.layout.dialog_create_map, null)
        val dialog =
            AlertDialog.Builder(this)
                .setTitle("Map title")
                .setView(mapFormView)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Ok", null)
                .show()

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val title = mapFormView.findViewById<EditText>(R.id.etTitle).text.toString()

            if (title.trim().isEmpty()) {
                Toast.makeText(this, "Map must have non-empty title", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            //Navigate to create map activity

            val intent = Intent(this@MainActivity, CreateMapActivity::class.java)
            intent.putExtra(EXTRA_MAP_TITLE, title)
            startActivityForResult(intent, REQUEST_CODE)
            dialog.dismiss()

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Get new map data from the data
            val userMap = data?.getSerializableExtra(EXTRA_USER_MAP) as UserMap
            Log.i(TAG, "onActivityResult with new map title ${userMap.title}")
            userMaps.add(userMap)
            mapAdapter.notifyItemInserted(userMaps.size - 1)
            serializeUserMaps(this, userMaps)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun serializeUserMaps(context: Context, userMaps: List<UserMap>) {
        Log.i(TAG, "serializeUserMaps")
        ObjectOutputStream(FileOutputStream(getDataFile(context))).use { it.writeObject(userMaps) }
    }

    private fun deserializeUserMaps(context: Context) : List<UserMap> {
        Log.i(TAG, "deserializeUserMaps")
        val dataFile = getDataFile(context)
        if (!dataFile.exists()) {
            Log.i(TAG, "Data file does not exist yet")
            return generateSampleData()


        }
        ObjectInputStream(FileInputStream(dataFile)).use {return it.readObject() as List<UserMap> }
    }
    private fun getDataFile(context: Context) : File {
        Log.i(TAG,"Getting file from directory ${context.filesDir}")
        return File(context.filesDir,FILENAME)
    }

    private fun generateSampleData(): List<UserMap> {
        return listOf(
            UserMap(
                "Hot Springs Of BC Guide",
                listOf()
            ),
            UserMap(
                "Undeveloped Springs",
                listOf(
                    Place("Pitt River Hot Springs", "", 49.694729, -122.706985),
                    Place("Sloquet Hot Springs", " ", 49.75909, -122.230453),
                    Place("Clear Creek", "", 49.695396, -121.732979),
                    Place("Keyhole/Pebble Creek Hot Springs", "\"https://hotspringsofbc.ca/keyhole-hot-springs\"\"> Click Here For More Info </a>", 50.668131, -123.460576),
                    Place("Placid hot springs", "", 50.568646, -123.473878),
                    Place("No Good warm springs", "", 50.561118, -123.526299),
                    Place("Baker Hot Spring", "", 48.727209, -121.690063),
                    Place("Sulphur Hot Springs", "", 48.256227, -121.194305),
                    Place("Kennedy Hot Spring", "", 48.156925, -121.289749),
                    Place("Gamma Hot Springs", "", 48.171123, -121.081696),
                    Place("Angel Warm Springs", "", 49.822084, -119.366356),
                    Place("Octopus Creek Hot Springs", "", 49.736469, -118.079725),
                    Place("St. Leon Hot Springs", "", 50.436079, -117.858582),
                    Place("Upper Halfway River Warm Springs", "", 50.49801860418479, -117.65496978598023),
                    Place("Taylor Warm Spring", "", 50.078295, -117.971191),
                    Place("Wilson Lake Warm Springs", "", 50.226128, -117.564628),
                    Place("Crawford Creek Warm Springs", "", 49.71124423085244, -116.76093550799561),
                    Place("Dewar Creek Hot Springs", "", 49.95406390501944, -116.51588893145752),
                    Place("Fording Mountain (Sulphur) Warm Springs", "", 49.994278, -114.898281),
                    Place("Wild Horse Warm Springs", "", 49.816056, -115.481415),
                    Place("Lussier Hot Springs", "", 50.13522498789943, -115.57688553107455),
                    Place("Ram Creek Hot Springs", "", 50.03369936865966, -115.59179283593753),
                    Place("Buhl Creek Hot Springs", "", 49.96361679493959, -116.02596743975833),
                    Place("Red Rock Cool Springs", "", 50.20811, -115.712814),
                    Place("Kidney Spring", "", 51.151722, -115.560733),
                    Place("Middle Springs", "", 51.16737, -115.582995),
                    Place("Vermilion Lakes Cool Spring", "", 51.17676, -115.630417),
                    Place("Mist Mountain Hot Spring", "", 50.53438, -114.889698),
                    Place("Miette Hot Springs", "", 53.132766, -117.766571),
                    Place("Kinbasket Lake (Canoe River) Hot Springs", "", 52.629882, -118.989233),
                    Place("Sheemahant Hot Springs", "", 51.798629, -126.450343),
                    Place("Tallheo Hot Springs", "", 52.211624, -126.941273),
                    Place("Tallheo Hot Springs 2", "", 52.206244, -126.937905),
                    Place("Thorsen Creek Hot Springs", "", 52.339535, -126.653137),
                    Place("Nascall Hot Springs", "", 52.49843310000001, -127.2707127),
                    Place("Eucott Bay Hot Springs 2", "", 52.445596, -127.323095),
                    Place("Eucott Bay Hot Springs ", "", 52.453348, -127.313719),
                    Place("Khutze Inlet Warm Springs", "", 53.084028, -128.400882),
                    Place("Klekane Inlet hot springs", "", 53.247267, -128.680634),
                    Place("Goat Harbour Hot Spring", "", 53.359411, -128.879444),
                    Place("Bishop Bay Hot Springs", "", 53.470509, -128.836147),
                    Place("Shearwater (Europa Bay) Hot Springs", "", 53.449896, -128.568248),
                    Place("Brim River Hot Springs", "", 53.513368, -128.369751),
                    Place("Weewanie Hot Springs", "", 53.695922, -128.787918),
                    Place("Aiyansh Hot Springs", "", 55.139071, -129.35347),
                    Place("Burton Creek Hot Springs", "", 54.948442, -129.80896),
                    Place("Frizzell Hotsprings", "", 54.199982, -129.8667273),
                    Place("Tchentlo Lake Warm Springs", "", 55.227741, -125.249901),
                    Place("Prophet River Hot Springs", "", 57.65083, -124.023333),
                    Place("Toad Hot Springs", "", 58.923764, -125.091728),
                    Place("Grayling River Hotsprings", "", 59.617769, -125.555878),
                    Place("Portage Brûlé Hot Springs", "", 59.655113, -126.95128),
                    Place("Sphaler Creek Warm Springs", "", 57.016811, -131.308614),
                    Place("Choquette Hot Springs Provincial Park", "", 56.832682, -131.756209),
                    Place("Barnes Lake (Paradise) Warm Springs", "", 56.677733, -131.888809),
                    Place("Len King Hot Springs", "", 56.428672, -130.638385),
                    Place("Mess Creek Hot Springs", "", 57.404354, -130.918245),
                    Place("Taweh Creek Hot Springs", "", 57.714866, -130.709474),
                    Place("Elwyn Creek Hot Springs", "", 57.789161, -130.723572),
                    Place("Atlin Warm Springs", "", 59.4038743, -133.5755377),
                    Place("Nakina Warm Springs", "", 59.130863, -132.978516),
                    Place("McArthur Hot Springs", "", 63.059937, -135.86792),
                    Place("Nash Creek Hot Springs", "", 63.6121, -135.878906),
                    Place("Larson North & South Hot Springs", "", 60.042904, -125.46936),
                    Place("Pool Creek Hot Springs", "", 60.34326, -125.513306),
                    Place("Kraus (Clausen Creek) Hotsprings", "", 61.220024, -124.030151),
                    Place("Rabbitkettle Hotsprings", "", 61.950092203167166, -127.17292740722657),
                    Place("West Cantung Hot Springs", "", 61.946702, -128.207703),
                    Place("East Cantung Warm Springs", "", 61.957033, -128.166504),
                    Place("North Cantung Warm Springs", "", 62.084601, -128.40271),
                    Place("Moore's Hotspring", "", 62.341961, -128.18573),
                    Place("Nahanni North Hot Springs", "", 62.395461, -128.696594),
                    Place("Nahanni Headwater Hot Springs", "", 662.803723, -128.905334),
                    Place("Broken Skull Hot Springs", "", 62.816274, -128.040161),
                    Place("Grizzly Bear Hot Springs", "", 62.657748, -127.924805),
                    Place("Ekwi Hot Springs", "", 64.038554, -128.122559),
                    Place("Deca Warm Springs", "", 64.172894, -128.397217),
                    Place("South Redstone Hot Springs", "", 63.396442, -125.969238),
                    Place("Roch-qui-trempe-à-l'eau Warm Springs", "", 63.283062, -123.563232),
                    Place("Deer River Hot Springs", "", 59.504076, -125.956106),
                    Place("Hobo Hot Springs", "", 49.303516, -121.791239),
                    Place("Fosthall Hot Springs", "", 50.383333, -117.933333),
                    Place("Meager Creek Hot Springs", "", 50.575486865389536, -123.46508481349184),
                    Place("Olympic Hot Springs", "", 47.978439, -123.678583)


                )
            ),
            UserMap("Partly Developed Springs",
                listOf(
                    Place("Tsek/Skookumchuck Hot Springs", " ", 49.9667582, -122.434906),
                    Place("Hot Springs Cove", "", 49.348612, -126.260977),
                    Place("Scenic Hot Springs", "", 47.710455, -121.141949),
                    Place("Aiyansh Hot Springs", "", 55.139071,-129.35347),
                    Place("Hotspring Island", "", 52.575498, -131.442379),
                    Place("Aiyansh Hot Springs", "", 55.139071,-129.35347),
                    Place("Chief Shakes Hot Springs", "", 56.722971, -132.032318)
                )
            ),
            UserMap("Developed Springs",
                listOf(
                    Place("Harrison Hot Springs", "", 49.3036798, -121.7897531),
                    Place("Ainsworth Hot Springs", "Ainsworth Hot Springs Resort", 49.73555529, -116.9113443),
                    Place("Nakusp Hot Springs", "", 50.295042, -117.686234),
                    Place("Halcyon Hot Springs", "", 50.516919, -117.898407),
                    Place("Canyon Hot Springs", "", 51.138102, -117.856261),
                    Place("Fairmont Hot Springs", "Fairmont Hot Springs Resort", 50.3284092, -115.843314),
                    Place("Radium Hot Springs", "", 50.6346824, -116.0390365),
                    Place("Cave and Basin National Historic Site", "", 51.170087, -115.589218),
                    Place("Banff Upper Hot Springs", "", 51.15114, -115.562096),
                    Place("Wild Horse Warm Springs", "", 49.816056, -115.481415),
                    Place("Lakelse (Mount Layton) Hot Springs", "", 54.5318479, -128.5197989),
                    Place("Liard River Hot Springs Provincial Park", "", 59.422378, -126.096268),
                    Place("Takhini Hot Springs", "", 60.879681, -135.359802),
                    Place("Sol Duc Hot Springs", "", 47.968253, -123.863495),
                    Place("Roch-qui-trempe-à-l'eau Warm Springs", "", 63.2830621, -123.5673197),
                    Place("Canyon Hot Springs Source", "", 51.126900, -117.847346)
                )
            ),
            UserMap(
                "All Springs",
                listOf(
                    Place("Harrison Hot Springs", "", 49.3036798, -121.7897531),
                    Place(
                        "Ainsworth Hot Springs",
                        "Ainsworth Hot Springs Resort",
                        49.73555529,
                        -116.9113443,
                    ),
                    Place("Nakusp Hot Springs", "", 50.295042, -117.686234),
                    Place("Halcyon Hot Springs", "", 50.516919, -117.898407),
                    Place("Canyon Hot Springs", "", 51.138102, -117.856261),
                    Place(
                        "Fairmont Hot Springs",
                        "Fairmont Hot Springs Resort",
                        50.3284092,
                        -115.843314,
                    ),
                    Place("Radium Hot Springs", "", 50.6346824, -116.0390365),
                    Place("Cave and Basin National Historic Site", "", 51.170087, -115.589218),
                    Place("Banff Upper Hot Springs", "", 51.15114, -115.562096),
                    Place("Wild Horse Warm Springs", "", 49.816056, -115.481415),
                    Place("Lakelse (Mount Layton) Hot Springs", "", 54.5318479, -128.5197989),
                    Place("Liard River Hot Springs Provincial Park", "", 59.422378, -126.096268),
                    Place("Takhini Hot Springs", "", 60.879681, -135.359802),
                    Place("Sol Duc Hot Springs", "", 47.968253, -123.863495),
                    Place("Roch-qui-trempe-à-l'eau Warm Springs", "", 63.2830621, -123.5673197),
                    Place("Canyon Hot Springs Source", "", 51.126900, -117.847346),
                    Place("Tsek/Skookumchuck Hot Springs", " ", 49.9667582, -122.434906),
                    Place("Hot Springs Cove", "", 49.348612, -126.260977),
                    Place("Scenic Hot Springs", "", 47.710455, -121.141949),
                    Place("Aiyansh Hot Springs", "", 55.139071, -129.35347),
                    Place("Hotspring Island", "", 52.575498, -131.442379),
                    Place("Aiyansh Hot Springs", "", 55.139071, -129.35347),
                    Place("Chief Shakes Hot Springs", "", 56.722971, -132.032318),
                    Place("Pitt River Hot Springs", "", 49.694729, -122.706985),
                    Place("Sloquet Hot Springs", " ", 49.75909, -122.230453),
                    Place("Clear Creek", "", 49.695396, -121.732979),
                    Place(
                        "Keyhole/Pebble Creek Hot Springs",
                        "\"https://hotspringsofbc.ca/keyhole-hot-springs\"\"> Click Here For More Info </a>",
                        50.668131,
                        -123.460576,
                    ),
                    Place("Placid hot springs", "", 50.568646, -123.473878),
                    Place("No Good warm springs", "", 50.561118, -123.526299),
                    Place("Baker Hot Spring", "", 48.727209, -121.690063),
                    Place("Sulphur Hot Springs", "", 48.256227, -121.194305),
                    Place("Kennedy Hot Spring", "", 48.156925, -121.289749),
                    Place("Gamma Hot Springs", "", 48.171123, -121.081696),
                    Place("Angel Warm Springs", "", 49.822084, -119.366356),
                    Place("Octopus Creek Hot Springs", "", 49.736469, -118.079725),
                    Place("St. Leon Hot Springs", "", 50.436079, -117.858582),
                    Place(
                        "Upper Halfway River Warm Springs",
                        "",
                        50.49801860418479,
                        -117.65496978598023,
                    ),
                    Place("Taylor Warm Spring", "", 50.078295, -117.971191),
                    Place("Wilson Lake Warm Springs", "", 50.226128, -117.564628),
                    Place(
                        "Crawford Creek Warm Springs",
                        "",
                        49.71124423085244,
                        -116.76093550799561,
                    ),
                    Place("Dewar Creek Hot Springs", "", 49.95406390501944, -116.51588893145752),
                    Place("Fording Mountain (Sulphur) Warm Springs", "", 49.994278, -114.898281),
                    Place("Wild Horse Warm Springs", "", 49.816056, -115.481415),
                    Place("Lussier Hot Springs", "", 50.13522498789943, -115.57688553107455),
                    Place("Ram Creek Hot Springs", "", 50.03369936865966, -115.59179283593753),
                    Place("Buhl Creek Hot Springs", "", 49.96361679493959, -116.02596743975833),
                    Place("Red Rock Cool Springs", "", 50.20811, -115.712814),
                    Place("Kidney Spring", "", 51.151722, -115.560733),
                    Place("Middle Springs", "", 51.16737, -115.582995),
                    Place("Vermilion Lakes Cool Spring", "", 51.17676, -115.630417),
                    Place("Mist Mountain Hot Spring", "", 50.53438, -114.889698),
                    Place("Miette Hot Springs", "", 53.132766, -117.766571),
                    Place("Kinbasket Lake (Canoe River) Hot Springs", "", 52.629882, -118.989233),
                    Place("Sheemahant Hot Springs", "", 51.798629, -126.450343),
                    Place("Tallheo Hot Springs", "", 52.211624, -126.941273),
                    Place("Tallheo Hot Springs 2", "", 52.206244, -126.937905),
                    Place("Thorsen Creek Hot Springs", "", 52.339535, -126.653137),
                    Place("Nascall Hot Springs", "", 52.49843310000001, -127.2707127),
                    Place("Eucott Bay Hot Springs 2", "", 52.445596, -127.323095),
                    Place("Eucott Bay Hot Springs ", "", 52.453348, -127.313719),
                    Place("Khutze Inlet Warm Springs", "", 53.084028, -128.400882),
                    Place("Klekane Inlet hot springs", "", 53.247267, -128.680634),
                    Place("Goat Harbour Hot Spring", "", 53.359411, -128.879444),
                    Place("Bishop Bay Hot Springs", "", 53.470509, -128.836147),
                    Place("Shearwater (Europa Bay) Hot Springs", "", 53.449896, -128.568248),
                    Place("Brim River Hot Springs", "", 53.513368, -128.369751),
                    Place("Weewanie Hot Springs", "", 53.695922, -128.787918),
                    Place("Aiyansh Hot Springs", "", 55.139071, -129.35347),
                    Place("Burton Creek Hot Springs", "", 54.948442, -129.80896),
                    Place("Frizzell Hotsprings", "", 54.199982, -129.8667273),
                    Place("Tchentlo Lake Warm Springs", "", 55.227741, -125.249901),
                    Place("Prophet River Hot Springs", "", 57.65083, -124.023333),
                    Place("Toad Hot Springs", "", 58.923764, -125.091728),
                    Place("Grayling River Hotsprings", "", 59.617769, -125.555878),
                    Place("Portage Brûlé Hot Springs", "", 59.655113, -126.95128),
                    Place("Sphaler Creek Warm Springs", "", 57.016811, -131.308614),
                    Place("Choquette Hot Springs Provincial Park", "", 56.832682, -131.756209),
                    Place("Barnes Lake (Paradise) Warm Springs", "", 56.677733, -131.888809),
                    Place("Len King Hot Springs", "", 56.428672, -130.638385),
                    Place("Mess Creek Hot Springs", "", 57.404354, -130.918245),
                    Place("Taweh Creek Hot Springs", "", 57.714866, -130.709474),
                    Place("Elwyn Creek Hot Springs", "", 57.789161, -130.723572),
                    Place("Atlin Warm Springs", "", 59.4038743, -133.5755377),
                    Place("Nakina Warm Springs", "", 59.130863, -132.978516),
                    Place("McArthur Hot Springs", "", 63.059937, -135.86792),
                    Place("Nash Creek Hot Springs", "", 63.6121, -135.878906),
                    Place("Larson North & South Hot Springs", "", 60.042904, -125.46936),
                    Place("Pool Creek Hot Springs", "", 60.34326, -125.513306),
                    Place("Kraus (Clausen Creek) Hotsprings", "", 61.220024, -124.030151),
                    Place("Rabbitkettle Hotsprings", "", 61.950092203167166, -127.17292740722657),
                    Place("West Cantung Hot Springs", "", 61.946702, -128.207703),
                    Place("East Cantung Warm Springs", "", 61.957033, -128.166504),
                    Place("North Cantung Warm Springs", "", 62.084601, -128.40271),
                    Place("Moore's Hotspring", "", 62.341961, -128.18573),
                    Place("Nahanni North Hot Springs", "", 62.395461, -128.696594),
                    Place("Nahanni Headwater Hot Springs", "", 662.803723, -128.905334),
                    Place("Broken Skull Hot Springs", "", 62.816274, -128.040161),
                    Place("Grizzly Bear Hot Springs", "", 62.657748, -127.924805),
                    Place("Ekwi Hot Springs", "", 64.038554, -128.122559),
                    Place("Deca Warm Springs", "", 64.172894, -128.397217),
                    Place("South Redstone Hot Springs", "", 63.396442, -125.969238),
                    Place("Roch-qui-trempe-à-l'eau Warm Springs", "", 63.283062, -123.563232),
                    Place("Deer River Hot Springs", "", 59.504076, -125.956106),
                    Place("Hobo Hot Springs", "", 49.303516, -121.791239),
                    Place("Fosthall Hot Springs", "", 50.383333, -117.933333),
                    Place("Meager Creek Hot Springs", "", 50.575486865389536, -123.46508481349184),
                    Place("Olympic Hot Springs", "", 47.978439, -123.678583),
                )
            )
        )
    }
}

