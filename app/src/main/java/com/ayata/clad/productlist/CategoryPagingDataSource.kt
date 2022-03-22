package com.ayata.clad.productlist

import android.util.Log
import androidx.paging.PagingSource
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.network.interceptor.EmptyException
import com.ayata.clad.home.response.ProductDetail
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import java.lang.reflect.Type

class CategoryPagingDataSource(private val caategoryId: Int, private val api: ApiService) :
    PagingSource<Int, ProductDetail>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProductDetail> {
        val position = params.key ?: 1
        return try {
            Log.d("calledme", "load: ");
            val response = api.categoryProductListAPI(caategoryId, position)
            try {
                val gson = Gson()
                val type: Type =
                    object : TypeToken<List<ProductDetail?>?>() {}.type
                val productList: List<ProductDetail> =
                    gson.fromJson(response.body()?.get("products"), type)
                if(response.body()?.get("total_pages").toString()==position.toString()){
                    LoadResult.Page(
                        data = productList,
                        prevKey = if (position == 1) null else position - 1,
                        nextKey = null
                    )
                }else {
                    LoadResult.Page(
                        data = productList,
                        prevKey = if (position == 1) null else position - 1,
                        nextKey = if (productList.isEmpty()) null else position + 1
                    )
                }
            }catch (e:Exception){
                try {
                    val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                    Log.d("testspaging", "load: "+jsonObj.getString("message"));
                    if("No product" in jsonObj.getString("message").toString()){
                        LoadResult.Error(EmptyException(response.body()?.get("message").toString()))
                    }else{
                        LoadResult.Error(e)
                    }
                }catch (e1:Exception){
                    LoadResult.Error(e1)
                }
            }
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }
}

