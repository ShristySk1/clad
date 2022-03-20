package com.ayata.clad.search.paging

import android.util.Log
import androidx.paging.PagingSource
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.home.response.ProductDetail
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.HttpException
import java.io.IOException
import java.lang.reflect.Type

class SearchPagingDataSource(
    private val auth: String,
    private val query: String,
    private val api: ApiService
) : PagingSource<Int, ProductDetail>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProductDetail> {
        val position = params.key ?: 1
        return try {
            Log.d("calledme", "load: ");
            val response = api.searchProduct(auth, query, position)
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
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }catch (exception:Exception){
            LoadResult.Error(exception)
        }
    }
}

