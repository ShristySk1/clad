package com.ayata.clad.view_all.paging

import android.util.Log
import androidx.paging.PagingSource
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.network.interceptor.EmptyException
import com.ayata.clad.home.response.Brand
import com.ayata.clad.home.response.ProductDetail
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import java.lang.reflect.Type

class BrandPagingDataSource(private val auth:String, private val title:String, private val api: ApiService): PagingSource<Int, Brand>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Brand> {
        val position = params.key ?: 1
        return try {
            Log.d("calledme", "load: ");
            val response = api.productAllAPI(auth, position,title)
            try {
                val gson = Gson()
                val type: Type =
                    object : TypeToken<List<Brand?>?>() {}.type
                val productList: List<Brand> =
                    gson.fromJson(response.body()?.get("brands"), type)
                if(productList.size>0){
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
                }else{
                    LoadResult.Error(EmptyException(response.body()?.get("message").toString()))
                }

            }catch (e: Exception){
                try {
                    if("empty" in response.body()?.get("message").toString()){
                        LoadResult.Error(EmptyException(response.body()?.get("message").toString()))
                    }else{
                        LoadResult.Error(e)
                    }
                }catch (e1: Exception){
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

