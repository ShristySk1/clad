package com.ayata.clad.productlist

import android.util.Log
import androidx.paging.PagingSource
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.network.interceptor.EmptyException
import com.ayata.clad.filter.FragmentFilter
import com.ayata.clad.home.response.ProductDetail
import com.ayata.clad.productlist.response.Color
import com.ayata.clad.productlist.response.MySize
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.lang.reflect.Type
import java.util.*


class CategoryPagingDataSource(
    private val token: String,
    private val queryMap: Map<String, String>,
    private val api: ApiService
) :
    PagingSource<Int, ProductDetail>() {


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProductDetail> {
        Log.d("calledme", "load:tetst ");
        val position = params.key ?: 1
        val response = api.categoryProductListAPI(token, queryMap, position)
        return try {
            Log.d("calledme", "load: ");
//            if (response.code() == 200){
//                Log.d("calledme", "200: ");
                try {
                    val gson = Gson()
                    val type: Type =
                        object : TypeToken<List<ProductDetail?>?>() {}.type
                    val productList: List<ProductDetail> =
                        gson.fromJson(response.body()?.get("products"), type)
                    if (productList.isNotEmpty()) {
                        //color
                        val jsonProducts = response.body()?.get("colors")?.asJsonArray
                        val type2: Type =
                            object : TypeToken<ArrayList<Color?>?>() {}.type
                        val listColor: ArrayList<Color> = Gson().fromJson(
                            jsonProducts,
                            type2
                        )
                        FragmentFilter.setMyColorListFromApi(listColor)
                        //size
//                val sizes = Gson().fromJson(response.body()?.get("sizes"), Sizes::class.java)
                        val retMap: Map<String, List<MySize>> = Gson().fromJson(
                            response.body()?.get("sizes"),
                            object : TypeToken<HashMap<String?, List<MySize>?>?>() {}.type
                        )
                        FragmentFilter.setMySizeListFromApi(retMap)
                        if (response.body()?.get("total_pages").toString() == position.toString()) {
                            LoadResult.Page(
                                data = productList,
                                prevKey = if (position == 1) null else position - 1,
                                nextKey = null
                            )
                        } else {
                            LoadResult.Page(
                                data = productList,
                                prevKey = if (position == 1) null else position - 1,
                                nextKey = if (productList.isEmpty()) null else position + 1
                            )
                        }
                    } else {
                        Log.d("calledme", "load: empty");
                        LoadResult.Page(
                            data = productList,
                            prevKey = if (position == 1) null else position - 1,
                            nextKey = null
                        )
                    }
                } catch (e: Exception) {
                    try {
                        val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                        Log.d("testspaging", "load: " + jsonObj.getString("message"));
                        if ("No product" in jsonObj.getString("message").toString()) {
                            LoadResult.Error(
                                EmptyException(
                                    response.body()?.get("message").toString()
                                )
                            )
                        } else {
                            LoadResult.Error(e)
                        }
                    } catch (e1: Exception) {
                        Log.d("myexception", "load: e1 " + e1.message);
                        LoadResult.Error(e1)
                    }
                }

//        }else{
//            Log.d("testdataerro", "load: ");
//                LoadResult.Error(EmptyException(response.body()?.get("message").toString()))
//            }
        } catch (exception: IOException) {
            Log.d("myexception", "load: exception" + response.code());

            Log.d("myexception", "load: exception" + exception.message);
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            Log.d("myexception http", "load: " + exception.message);
            LoadResult.Error(exception)
        }
    }
}

