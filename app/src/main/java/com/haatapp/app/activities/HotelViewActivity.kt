package com.haatapp.app.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.CursorLoader
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.*
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.ViewSkeletonScreen
import com.sackcentury.shinebuttonlib.ShineButton
import com.haatapp.app.HeaderView
import com.haatapp.app.R
import com.haatapp.app.adapter.HotelCatagoeryAdapter
import com.haatapp.app.build.api.ApiClient
import com.haatapp.app.build.api.ApiInterface
import com.haatapp.app.helper.ConnectionHelper
import com.haatapp.app.helper.CustomDialog
import com.haatapp.app.helper.GlobalData
import com.haatapp.app.models.AddCart
import com.haatapp.app.models.Category
import com.haatapp.app.models.Favorite
import com.haatapp.app.models.Prescription
import com.haatapp.app.models.Product
import com.haatapp.app.models.Shop
import com.haatapp.app.models.ShopDetail
import com.haatapp.app.utils.Utils

import org.json.JSONObject

import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.ArrayList
import java.util.HashMap

import butterknife.BindView
import butterknife.ButterKnife
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

import com.haatapp.app.adapter.HotelCatagoeryAdapter.bottomSheetDialogFragment

class HotelViewActivity : AppCompatActivity(), AppBarLayout.OnOffsetChangedListener {

    @BindView(R.id.toolbar)
    internal var toolbar: Toolbar ?=null
    @BindView(R.id.recommended_dishes_rv)
    internal var recommendedDishesRv: RecyclerView? = null
    @BindView(R.id.accompaniment_dishes_rv)
    internal var accompanimentDishesRv: RecyclerView? = null
    @BindView(R.id.heart_btn)
    internal var heartBtn: ShineButton? = null
    @BindView(R.id.view_line)
    internal var viewLine: View? = null
    @BindView(R.id.restaurant_title2)
    internal var restaurantTitle2: TextView? = null
    @BindView(R.id.restaurant_subtitle2)
    internal var restaurantSubtitle2: TextView? = null
    @BindView(R.id.restaurant_image)
    internal var restaurantImage: ImageView? = null
    @BindView(R.id.header_view_title)
    internal var headerViewTitle: TextView? = null
    @BindView(R.id.header_view_sub_title)
    internal var headerViewSubTitle: TextView? = null
    @BindView(R.id.collapsing_toolbar)
    internal var collapsingToolbar: CollapsingToolbarLayout? = null
    @BindView(R.id.app_bar_layout)
    internal var appBarLayout: AppBarLayout? = null
    @BindView(R.id.scroll)
    internal var scroll: NestedScrollView? = null
    @BindView(R.id.offer)
    internal var offer: TextView? = null
    @BindView(R.id.tabLayout)
    internal var tabLayout: TabLayout?=null
    @BindView(R.id.addmore)
    internal var addMore: Button? = null


    @BindView(R.id.line2)
    internal var line2: LinearLayout? = null


    @BindView(R.id.line)
    internal var line: LinearLayout? = null

    @BindView(R.id.rating)
    internal var rating: TextView? = null
    @BindView(R.id.delivery_time)
    internal var deliveryTime: TextView? = null
    @BindView(R.id.root_layout)
    internal var rootLayout: CoordinatorLayout? = null
    private var isHideToolbarView = false
    @BindView(R.id.toolbar_header_view)
    internal var toolbarHeaderView: HeaderView? = null
    @BindView(R.id.float_header_view)
    internal var floatHeaderView: HeaderView? = null
    internal var restaurantPosition = 0
    internal var isShopIsChanged = true
    internal var priceAmount = 0
    internal var itemCount = 0
    internal var itemQuantity = 0
    internal var slide_down: Animation?=null
    internal var slide_up: Animation?=null


    internal var context: Context?=null
    internal var connectionHelper: ConnectionHelper?=null
    internal var activity: Activity?=null
    internal var apiInterface = ApiClient.getRetrofit().create<ApiInterface>(ApiInterface::class.java!!)
    internal var skeleton: ViewSkeletonScreen?=null
    internal var isFavourite = false

    internal var linear_image: LinearLayout?=null
    internal var uploadimage: Button?=null
    internal var btnsendprescr: Button?=null
    internal var editor: SharedPreferences.Editor?=null
    private val REQUEST_CAMERA = 0
    private val SELECT_FILE = 1
    private var ivImage: ImageView? = null
    private val userChoosenTask: String? = null
    internal var imgFile: File? = null
    internal var customDialog: CustomDialog? = null
    private val PICK_IMAGE_REQUEST = 1
    internal var strshopid: String?=null
    internal var str_userid: String?=null
    internal var filepath = ""

    internal var allEdProduct: MutableList<EditText> = ArrayList()
    internal var allEdQty: MutableList<EditText> = ArrayList()
    internal var product_name: EditText?=null
    internal var product_qty: EditText?=null

    internal var allEdpro: MutableList<String> = ArrayList()
    internal var allEdQ: MutableList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotel_view)

        toolbar =findViewById(R.id.toolbar)
        appBarLayout =findViewById(R.id.app_bar_layout)
        addMore =findViewById(R.id.addmore)
        offer =findViewById(R.id.offer)
        recommendedDishesRv =findViewById(R.id.recommended_dishes_rv)
        accompanimentDishesRv =findViewById(R.id.accompaniment_dishes_rv)
        heartBtn =findViewById(R.id.heart_btn)
        viewLine =findViewById(R.id.view_line)
        restaurantTitle2 =findViewById(R.id.restaurant_title2)
        restaurantSubtitle2 =findViewById(R.id.restaurant_subtitle2)
        restaurantImage =findViewById(R.id.restaurant_image)
        headerViewTitle =findViewById(R.id.header_view_title)
        headerViewSubTitle =findViewById(R.id.header_view_sub_title)
        collapsingToolbar =findViewById(R.id.collapsing_toolbar)
        appBarLayout =findViewById(R.id.app_bar_layout)
        scroll =findViewById(R.id.scroll)
        offer =findViewById(R.id.offer)
        addMore =findViewById(R.id.addmore)
        line2 =findViewById(R.id.line2)
        line =findViewById(R.id.line)
        rating =findViewById(R.id.rating)
        deliveryTime =findViewById(R.id.delivery_time)
        rootLayout =findViewById(R.id.root_layout)
        toolbarHeaderView =findViewById(R.id.toolbar_header_view)
        floatHeaderView =findViewById(R.id.float_header_view)




        ButterKnife.bind(this)
        context = this@HotelViewActivity
        activity = this@HotelViewActivity
        connectionHelper = ConnectionHelper(context)
        setSupportActionBar(toolbar)
    //    supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar!!.setNavigationOnClickListener { onBackPressed() }
        appBarLayout!!.addOnOffsetChangedListener(this)
        categoryList = ArrayList()
        shops = GlobalData.selectedShop

        Log.d("shop", "onCreate: **************************112212122")
        Log.d("shops", "onCreate: " + shops!!)
        strshopid = shops!!.id.toString()

        if (GlobalData.profileModel != null) {


            str_userid = GlobalData.profileModel.id.toString()

            Log.d("shopid", "onCreate: $strshopid")
            Log.d("userid", "onCreate: $str_userid")

        }


        val pref = activity!!.applicationContext.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        editor = pref.edit()
        val catid = pref.getString("catid", null)
        customDialog = CustomDialog(this)


        linear_image = findViewById<View>(R.id.linear_image) as LinearLayout
        uploadimage = findViewById<View>(R.id.uploadimage) as Button
        ivImage = findViewById<View>(R.id.profile) as ImageView
        btnsendprescr = findViewById<View>(R.id.btnsendprescr) as Button
        product_name = findViewById<View>(R.id.product_name) as EditText
        product_qty = findViewById<View>(R.id.product_qty) as EditText



        allEdProduct = ArrayList()
        allEdQty = ArrayList()


        allEdProduct!!.add(product_name!!)
        allEdQty!!.add(product_qty!!)


        if (catid == "1") {
            linear_image!!.visibility = View.VISIBLE
        } else if (catid == "4") {
            linear_image!!.visibility = View.VISIBLE
        } else {
            linear_image!!.visibility = View.GONE
        }



        addMore!!.setOnClickListener {
            val child = layoutInflater.inflate(R.layout.child, null)
            line2!!.addView(child)

            allEdProduct!!.add(product_name!!)
            allEdQty!!.add(product_qty!!)
            allEdProduct!!.add(product_name!!)
        }




        btnsendprescr!!.setOnClickListener { updateProfile() }
        uploadimage!!.setOnClickListener {
            //                selectImage();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this@HotelViewActivity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this@HotelViewActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    goToImageIntent()
                } else {
                    requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA), ASK_MULTIPLE_PERMISSION_REQUEST_CODE)
                }
            } else {
                goToImageIntent()
            }
        }



        if (shops != null) {
            //Load animation
            slide_down = AnimationUtils.loadAnimation(context,
                    R.anim.slide_down)
            slide_up = AnimationUtils.loadAnimation(context,
                    R.anim.slide_up)

            val intent = intent
            val bundle = intent.extras
            if (bundle != null) {
                restaurantPosition = bundle.getInt("position")

            }
            isFavourite = getIntent().getBooleanExtra("is_fav", false)

            if (shops!!.offerPercent == null) {
                offer!!.visibility = View.GONE
            } else {
                offer!!.visibility = View.VISIBLE
                offer!!.text = "Flat " + shops!!.offerPercent!!.toString() + "% offer on all Orders"
            }

            if (shops!!.ratings != null ) {
                val ratingvalue = BigDecimal(shops!!.ratings.rating).setScale(1, RoundingMode.HALF_UP).toDouble()
                rating!!.text = ratingvalue.toString()
            } else
                rating!!.text = "No Rating"

            deliveryTime!!.text = shops!!.estimatedDeliveryTime!!.toString() + "Mins"

            itemText = findViewById<View>(R.id.item_text) as TextView
            viewCartShopName = findViewById<View>(R.id.view_cart_shop_name) as TextView
            viewCart = findViewById<View>(R.id.view_cart) as TextView
            viewCartLayout = findViewById<View>(R.id.view_cart_layout) as RelativeLayout

            viewCartLayout!!.setOnClickListener {
                startActivity(Intent(this@HotelViewActivity, ViewCartActivity::class.java))
                overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing)
            }

            Glide.with(this@HotelViewActivity).load(shops!!.avatar)
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_restaurant_place_holder)
                    .error(R.drawable.ic_restaurant_place_holder)
                    .into(restaurantImage!!)

            //        Glide.with(context).load(shops.getAvatar()).placeholder(R.drawable.ic_restaurant_place_holder).dontAnimate()
            //                .error(R.drawable.ic_restaurant_place_holder).into(restaurantImage);

            //Set Palette color

//     if(shops!!.avatar!=null) {
//         Picasso.with(this@HotelViewActivity)
//                 .load(shops!!.avatar)
//                 .into(object : Target {
//                     override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
//                         assert(restaurantImage != null)
//                         restaurantImage!!.setImageBitmap(bitmap)
//                         Palette.from(bitmap)
//                                 .generate { palette ->
//                                     var textSwatch: Palette.Swatch? = palette.darkMutedSwatch
//                                     if (textSwatch == null) {
//                                         textSwatch = palette.mutedSwatch
//                                         if (textSwatch != null) {
//                                             collapsingToolbar!!.setContentScrimColor(textSwatch.rgb)
//                                             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                                                 val window = window
//                                                 window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//                                                 window.statusBarColor = textSwatch.rgb
//                                             }
//                                             headerViewTitle!!.setTextColor(textSwatch.titleTextColor)
//                                             headerViewSubTitle!!.setTextColor(textSwatch.bodyTextColor)
//                                         }
//                                     } else {
//                                         collapsingToolbar!!.setContentScrimColor(textSwatch.rgb)
//                                         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                                             val window = window
//                                             window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//                                             window.statusBarColor = textSwatch.rgb
//                                         }
//                                         headerViewTitle!!.setTextColor(textSwatch.titleTextColor)
//                                         headerViewSubTitle!!.setTextColor(textSwatch.bodyTextColor)
//                                     }
//                                 }
//
//                     }
//
//                     override fun onBitmapFailed(errorDrawable: Drawable) {
//
//                     }
//
//                     override fun onPrepareLoad(placeHolderDrawable: Drawable) {
//
//                     }
//                 })
//     }
            //Set title
            collapsingToolbar!!.title = " "
            toolbarHeaderView!!.bindTo(shops!!.name, shops!!.description)
            floatHeaderView!!.bindTo(shops!!.name, shops!!.description)

            //Set Categoery shopList adapter
            catagoeryAdapter = HotelCatagoeryAdapter(this, activity, categoryList)
            accompanimentDishesRv!!.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            accompanimentDishesRv!!.itemAnimator = DefaultItemAnimator()
            accompanimentDishesRv!!.adapter = catagoeryAdapter

            //        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            //            @Override
            //            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            //                Log.e("scrollX", "" + scrollX);
            //                Log.e("scrollY", "" + scrollY);
            //                Log.e("oldScrollX", "" + oldScrollX);
            //                Log.e("oldScrollY", "" + oldScrollY);
            //
            //                if (scrollY >= 50 && scrollY <= 280) {
            //                    int staticData = 280;
            //                    float alphaValue = (float) scrollY / staticData;
            //                    heartBtn.setAlpha(1 - alphaValue);
            //                    titleLayout.setAlpha(alphaValue);
            //                    viewLine.setAlpha(alphaValue);
            //                } else if (scrollY >= 280) {
            //                    heartBtn.setAlpha(0);
            //                    viewLine.setAlpha(1.0f);
            //                    titleLayout.setAlpha(1.0f);
            //                } else if (scrollY <= 50) {
            //                    viewLine.setAlpha(0);
            //                    titleLayout.setAlpha(0);
            //                    heartBtn.setAlpha(1.0f);
            //                }
            //
            //            }
            //        });

            //Heart Animation Button
            if (heartBtn != null)
                heartBtn!!.init(this)
            if (shops!!.favorite != null || isFavourite) {
                heartBtn!!.isChecked = true
                heartBtn!!.tag = 1
            } else
                heartBtn!!.tag = 0
            heartBtn!!.setShineDistanceMultiple(1.8f)
            heartBtn!!.setOnClickListener {
                if (heartBtn!!.tag == 0) {
                    heartBtn!!.tag = 1
                    heartBtn!!.setShapeResource(R.raw.heart)
                } else {
                    heartBtn!!.tag = 0
                    heartBtn!!.setShapeResource(R.raw.icc_heart)
                }
            }
            heartBtn!!.setOnCheckStateChangeListener { view, checked ->
                Log.e("HeartButton", "click $checked")
                if (connectionHelper!!.isConnectingToInternet) {
                    if (checked) {
                        if (GlobalData.profileModel != null)
                            doFavorite(shops!!.id)
                        else {
                            startActivity(Intent(context, LoginActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                            overridePendingTransition(R.anim.slide_in_left, R.anim.anim_nothing)
                            finish()
                        }
                    } else {
                        deleteFavorite(shops!!.id)
                    }
                } else {
                    Utils.displayMessage(activity, context, getString(R.string.oops_connect_your_internet))
                }
            }
            skeleton = Skeleton.bind(rootLayout)
                    .load(R.layout.skeleton_hotel_view)
                    .show()

        } else {
            startActivity(Intent(context, SplashActivity::class.java))
            finish()
        }


    }


    private fun deleteFavorite(id: Int?) {
        val call = apiInterface.deleteFavorite(id!!)
        call.enqueue(object : Callback<Favorite> {
            override fun onResponse(call: Call<Favorite>, response: Response<Favorite>) {
                val favorite = response.body()
            }

            override fun onFailure(call: Call<Favorite>, t: Throwable) {

            }
        })

    }

    private fun doFavorite(id: Int?) {
        val call = apiInterface.doFavorite(id!!)
        call.enqueue(object : Callback<Favorite> {
            override fun onResponse(call: Call<Favorite>, response: Response<Favorite>) {
                val favorite = response.body()
            }

            override fun onFailure(call: Call<Favorite>, t: Throwable) {

            }
        })

    }

    private fun setViewcartBottomLayout(addCart: AddCart) {
        priceAmount = 0
        itemQuantity = 0
        itemCount = 0
        //get Item Count
        itemCount = addCart.productList.size
        for (i in 0 until itemCount) {
            //Get Total item Quantity
            itemQuantity = itemQuantity + addCart.productList[i].quantity!!
            //Get product price
            if (addCart.productList[i].product.prices.price != null)
                priceAmount = priceAmount + addCart.productList[i].quantity!! * addCart.productList[i].product.prices.price!!
            if (addCart.productList[i].cartAddons != null && !addCart.productList[i].cartAddons.isEmpty()) {
                for (j in 0 until addCart.productList[i].cartAddons.size) {
                    priceAmount = priceAmount + addCart.productList[i].quantity!! * (addCart.productList[i].cartAddons[j].quantity!! * addCart.productList[i].cartAddons[j].addonProduct.price!!)
                }
            }
        }
        GlobalData.notificationCount = itemQuantity
        if (itemQuantity == 0) {
            HotelViewActivity.viewCartLayout!!.visibility = View.GONE
            // Start animation
            viewCartLayout!!.startAnimation(slide_down)
        } else if (itemQuantity == 1) {
            if (shops!!.id != GlobalData.addCart.productList[0].product.shopId) {
                HotelViewActivity.viewCartShopName!!.visibility = View.VISIBLE
                HotelViewActivity.viewCartShopName!!.text = "From : " + GlobalData.addCart.productList[0].product.shop.name
            } else
                HotelViewActivity.viewCartShopName!!.visibility = View.GONE
            val currency = addCart.productList[0].product.prices.currency
            HotelViewActivity.itemText!!.text = "$itemQuantity Item | $currency$priceAmount"
            if (HotelViewActivity.viewCartLayout!!.visibility == View.GONE) {
                // Start animation
                HotelViewActivity.viewCartLayout!!.visibility = View.VISIBLE
                viewCartLayout!!.startAnimation(slide_up)
            }
        } else {
            if (shops!!.id != GlobalData.addCart.productList[0].product.shopId) {
                HotelViewActivity.viewCartShopName!!.visibility = View.VISIBLE
                HotelViewActivity.viewCartShopName!!.text = "From : " + GlobalData.addCart.productList[0].product.shop.name
            } else
                HotelViewActivity.viewCartShopName!!.visibility = View.GONE

            val currency = addCart.productList[0].product.prices.currency
            HotelViewActivity.itemText!!.text = "$itemQuantity Items | $currency$priceAmount"
            if (HotelViewActivity.viewCartLayout!!.visibility == View.GONE) {
                // Start animation
                HotelViewActivity.viewCartLayout!!.visibility = View.VISIBLE
                viewCartLayout!!.startAnimation(slide_up)
            }

        }
    }

    private fun getCategories(map: HashMap<String, String>) {
        skeleton!!.show()
        val call = apiInterface.getCategories(map)
        call.enqueue(object : Callback<ShopDetail> {
            override fun onResponse(call: Call<ShopDetail>, response: Response<ShopDetail>) {
                skeleton!!.hide()
                if (response.isSuccessful) {

                    categoryList = ArrayList()
                    categoryList!!.clear()
                    val category = Category()
                    featureProductList = ArrayList()
                    featureProductList = response.body()!!.featuredProducts
                    category.name = resources.getString(R.string.featured_products)
                    category.products = featureProductList
                    categoryList!!.add(category)
                    categoryList!!.addAll(response.body()!!.categories)


                    GlobalData.categoryList = categoryList
                    GlobalData.selectedShop.categories = categoryList
                    catagoeryAdapter = HotelCatagoeryAdapter(context, activity, categoryList)
                    accompanimentDishesRv!!.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    accompanimentDishesRv!!.itemAnimator = DefaultItemAnimator()
                    accompanimentDishesRv!!.adapter = catagoeryAdapter
                    if (GlobalData.addCart != null && GlobalData.addCart.productList.size != 0) {
                        setViewcartBottomLayout(GlobalData.addCart)
                    } else {
                        viewCartLayout!!.visibility = View.GONE
                    }


                    catagoeryAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<ShopDetail>, t: Throwable) {

            }
        })


    }


    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onResume() {
        super.onResume()
        if (bottomSheetDialogFragment != null)
            bottomSheetDialogFragment.dismiss()

        if (connectionHelper!!.isConnectingToInternet) {
            //get User Profile Data
            if (GlobalData.profileModel != null) {
                val map = HashMap<String, String>()
                map["shop"] = shops!!.id.toString()
                map["user_id"] = GlobalData.profileModel.id.toString()
                getCategories(map)
            } else {
                val map = HashMap<String, String>()
                map["shop"] = shops!!.id.toString()
                getCategories(map)
            }
        } else {
            Utils.displayMessage(activity, context, getString(R.string.oops_connect_your_internet))
        }


    }
    //    @Override
    //    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    //        switch (requestCode) {
    //            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
    //                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
    //                    if(userChoosenTask.equals("Take Photo"))
    //                        cameraIntent();
    //                    else if(userChoosenTask.equals("Choose from Library"))
    //                        galleryIntent();
    //                } else {
    //                    //code for deny
    //                }
    //                break;
    //        }
    //    }
    //
    //    private void selectImage() {
    //        final CharSequence[] items = { "Take Photo", "Choose from Library",
    //                "Cancel" };
    //
    //        AlertDialog.Builder builder = new AlertDialog.Builder(HotelViewActivity.this);
    //        builder.setTitle("Add Photo!");
    //        builder.setItems(items, new DialogInterface.OnClickListener() {
    //            @Override
    //            public void onClick(DialogInterface dialog, int item) {
    //                boolean result=Utility.checkPermission(HotelViewActivity.this);
    //
    //                if (items[item].equals("Take Photo")) {
    //                    userChoosenTask ="Take Photo";
    //                    if(result)
    //                        cameraIntent();
    //
    //                } else if (items[item].equals("Choose from Library")) {
    //                    userChoosenTask ="Choose from Library";
    //                    if(result)
    //                        galleryIntent();
    //
    //                } else if (items[item].equals("Cancel")) {
    //                    dialog.dismiss();
    //                }
    //            }
    //        });
    //        builder.show();
    //    }
    //
    //    private void galleryIntent()
    //    {
    //        Intent intent = new Intent();
    //        intent.setType("image/*");
    //        intent.setAction(Intent.ACTION_GET_CONTENT);//
    //        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    //    }
    //
    //    private void cameraIntent()
    //    {
    //        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    //        startActivityForResult(intent, REQUEST_CAMERA);
    //    }
    //
    //    @Override
    //    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    //        super.onActivityResult(requestCode, resultCode, data);
    //
    //        if (resultCode == Activity.RESULT_OK) {
    //            if (requestCode == SELECT_FILE)
    //                onSelectFromGalleryResult(data);
    //            else if (requestCode == REQUEST_CAMERA)
    //                onCaptureImageResult(data);
    //        }
    //    }
    //
    //    private void onCaptureImageResult(Intent data) {
    //        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
    //        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    //        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
    //
    //        File destination = new File(Environment.getExternalStorageDirectory(),
    //                System.currentTimeMillis() + ".jpg");
    //
    //        FileOutputStream fo;
    //        try {
    //            destination.createNewFile();
    //            fo = new FileOutputStream(destination);
    //            fo.write(bytes.toByteArray());
    //            fo.close();
    //        } catch (FileNotFoundException e) {
    //            e.printStackTrace();
    //        } catch (IOException e) {
    //            e.printStackTrace();
    //        }
    //
    //        ivImage.setImageBitmap(thumbnail);
    //    }
    //
    //
    //
    //    @SuppressWarnings("deprecation")
    //    private void onSelectFromGalleryResult(Intent data) {
    //
    //        Bitmap bm=null;
    //        if (data != null) {
    //            try {
    //                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
    //            } catch (IOException e) {
    //                e.printStackTrace();
    //            }
    //        }
    //
    //        ivImage.setImageBitmap(bm);
    //    }


    fun goToImageIntent() {

        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val selectedImage = data.data
            filepath = getPath(selectedImage)
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

            // Get the cursor
            val cursor = contentResolver.query(selectedImage!!,
                    filePathColumn, null, null, null)
            // Move to first row
            assert(cursor != null)
            cursor!!.moveToFirst()

            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            val imgDecodableString = cursor.getString(columnIndex)
            cursor.close()
            // Set the Image in ImageView after decoding the String
            //userAvatar.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
            Glide.with(this).load(imgDecodableString).into(ivImage!!)
            imgFile = File(imgDecodableString)
        } else if (resultCode == Activity.RESULT_CANCELED) {
            //            Toast.makeText(this, "You haven't picked Image",Toast.LENGTH_SHORT).show();
        }

    }

    //    @OnClick({R.id.edit_user_profile, R.id.update})
    //    public void onViewClicked(View view) {
    //        switch (view.getId()) {
    //            case R.id.edit_user_profile:
    //                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    //                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
    //                        goToImageIntent();
    //                    } else {
    //                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
    //                    }
    //                } else {
    //                    goToImageIntent();
    //                }
    //                break;
    //            case R.id.update:
    //                if (connectionHelper.isConnectingToInternet()) {
    //                    updateProfile();
    //                } else {
    //                    Utils.displayMessage(activity, context, getString(R.string.oops_connect_your_internet));
    //                }
    //                break;
    //        }
    //    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            ASK_MULTIPLE_PERMISSION_REQUEST_CODE -> if (grantResults.size > 0) {
                val permission1 = grantResults[1] == PackageManager.PERMISSION_GRANTED
                val permission2 = grantResults[0] == PackageManager.PERMISSION_GRANTED

                if (permission1 && permission2) {
                    goToImageIntent()
                } else {
                    Snackbar.make(this.findViewById(android.R.id.content),
                            "Please Grant Permissions to upload Profile",
                            Snackbar.LENGTH_INDEFINITE).setAction("ENABLE"
                    ) { ActivityCompat.requestPermissions(this@HotelViewActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA), ASK_MULTIPLE_PERMISSION_REQUEST_CODE) }.show()
                }
            }
        }
    }


    private fun updateProfile() {

        if(imgFile!=null) {
            //   Log.d("Image ", "updateProfile: " + imgFile!!.name)
            if (customDialog != null)
                customDialog!!.show()
            val map = HashMap<String, RequestBody>()
            map["user_id"] = RequestBody.create(MediaType.parse("text/plain"), "1")
            map["shop_id"] = RequestBody.create(MediaType.parse("text/plain"), "2")



            for (i in allEdProduct.indices) {
                allEdpro.add(allEdProduct[i].text.toString())
            }

            for (i in allEdQty.indices) {
                allEdQ.add(allEdQty[i].text.toString())
            }

            var namesStr = StringBuilder()
            for (name in allEdpro) {
                namesStr = if (namesStr.length > 0) namesStr.append(",").append(name) else namesStr.append(name)
            }

            var qty = StringBuilder()
            for (name in allEdQ) {
                qty = if (qty.length > 0) qty.append(",").append(name) else qty.append(name)
            }

            var filePart: MultipartBody.Part? = null
            //   RequestBody requestBodyFile = RequestBody.create(MediaType.parse("image/*"), imgFile);
            val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imgFile!!)
            ////      RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);
            val user_id = RequestBody.create(MediaType.parse("text/plain"), str_userid)
            val shop_id = RequestBody.create(MediaType.parse("text/plain"), strshopid)
            val product_name = RequestBody.create(MediaType.parse("text/plain"), namesStr.toString())
            val prod_qty = RequestBody.create(MediaType.parse("text/plain"), qty.toString())

            if (imgFile != null)


            //      RequestBody requestBodyFile = RequestBody.create(MediaType.parse("image/*"), imgFile);
            //     filePart = MultipartBody.Part.createFormData("prescription",imgFile.getName(), RequestBody.create(MediaType.parse("image/*"), imgFile));
            //      filePart = MultipartBody.Part.createFormData("prescription", imgFile.getName(), RequestBody.create(MediaType.parse("image/*"), imgFile));

            //    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imgFile);
            // MultipartBody.Part is used to send also the actual file name
                filePart = MultipartBody.Part.createFormData("prescription", imgFile!!.name, requestFile)



            Log.d("filePart ", "updateProfile: " + filePart!!)
            val call = apiInterface.UploadOrder(user_id, shop_id, filePart, product_name, prod_qty)
            call.enqueue(object : Callback<Prescription> {
                override fun onResponse(call: Call<Prescription>, response: Response<Prescription>) {
                    customDialog!!.cancel()
                    if (response.isSuccessful) {

                        Toast.makeText(context, "" + resources.getString(R.string.profile_updated_successfully), Toast.LENGTH_SHORT).show()

                        finish()
                        overridePendingTransition(0, 0)
                        startActivity(intent)
                        overridePendingTransition(0, 0)

                    } else {
                        try {
                            Log.d("response ::", "onResponse: $response")

                            Log.d("Response ::", "onResponse: " + response.errorBody()!!.toString())

                            val jObjError = JSONObject(response.errorBody()!!.toString())
                            Log.d("try", "onResponse: " + jObjError.optString("error"))

                            Toast.makeText(context, "try :: " + jObjError.optString("error"), Toast.LENGTH_LONG).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "catch :: " + e.message, Toast.LENGTH_LONG).show()
                            Log.d("catch", "onResponse: " + e.message)
                        }

                    }
                }

                override fun onFailure(call: Call<Prescription>, t: Throwable) {
                    customDialog!!.cancel()
                    Toast.makeText(context, resources.getString(R.string.network_error_toast), Toast.LENGTH_SHORT).show()
                }
            })

        }

        else {

            Toast.makeText(this@HotelViewActivity,"Please select image",Toast.LENGTH_LONG).show()

        }

    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.anim_nothing, R.anim.slide_out_right)
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        val maxScroll = appBarLayout.totalScrollRange
        val percentage = Math.abs(verticalOffset).toFloat() / maxScroll.toFloat()

        if (percentage == 1f && isHideToolbarView) {
            toolbarHeaderView!!.visibility = View.VISIBLE
            isHideToolbarView = !isHideToolbarView

        } else if (percentage < 1f && !isHideToolbarView) {
            toolbarHeaderView!!.visibility = View.GONE
            isHideToolbarView = !isHideToolbarView
        }
    }

    private fun getPath(contentUri: Uri?): String {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val loader = CursorLoader(applicationContext, contentUri, proj, null, null, null)
        val cursor = loader.loadInBackground()
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val result = cursor.getString(column_index)
        cursor.close()
        return result
    }

    companion object {
        //    @BindView(R.id.scroll_view)
        //    NestedScrollView scrollView;
        var itemText: TextView?=null
        var viewCartShopName: TextView?=null
        var viewCart: TextView?=null
        var viewCartLayout: RelativeLayout?=null
        var shops: Shop? = null

        var categoryList: MutableList<Category>?=null
        var featureProductList: List<Product>?=null
        var catagoeryAdapter: HotelCatagoeryAdapter?=null
        private val ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 0
    }

}


