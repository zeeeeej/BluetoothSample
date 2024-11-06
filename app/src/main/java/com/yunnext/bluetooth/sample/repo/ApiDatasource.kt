//package com.yunnext.bluetooth.sample.repo
//
//import com.google.gson.Gson
//import com.yunnext.bluetooth.sample.domain.Product
//import com.yunnext.bluetooth.sample.domain.ProductModel
//import com.yunnext.bluetooth.sample.repo.http.Api
//import com.yunnext.bluetooth.sample.repo.http.ApiException
//import com.yunnext.bluetooth.sample.repo.http.ApiService
//import com.yunnext.bluetooth.sample.repo.resp.UserResp
//
//import kotlinx.serialization.json.Json
//import okhttp3.FormBody
//import okhttp3.MediaType
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.RequestBody
//import okhttp3.RequestBody.Companion.toRequestBody
//
//
//interface ApiDatasource {
//
//    suspend fun login(user: String, pwd: String): com.yunnext.bluetooth.sample.domain.HDResult<UserResp>
//    suspend fun logout(token: String): com.yunnext.bluetooth.sample.domain.HDResult<Boolean>
//    suspend fun series(token: String): com.yunnext.bluetooth.sample.domain.HDResult<List<Product>>
//    suspend fun models(token: String, seriesId: String): com.yunnext.bluetooth.sample.domain.HDResult<List<ProductModel>>
//    suspend fun check(token: String, code: String): com.yunnext.bluetooth.sample.domain.HDResult<ScanResultVo>
//    suspend fun finish(
//        token: String,
//        code: String,
//        componentCode: String,
//        productCode: String,
//        result: String
//    ): com.yunnext.bluetooth.sample.domain.HDResult<Boolean>
//
//}
//
//class ApiDatasourceImpl(private val apiService: ApiService = Api.apiService) : ApiDatasource {
//
//    override suspend fun login(user: String, pwd: String): com.yunnext.bluetooth.sample.domain.HDResult<UserResp> {
//        return try {
//            val httpResp = apiService.login(account = user, password = pwd)
//            if (httpResp.success) {
//                com.yunnext.bluetooth.sample.domain.HDResult.Success(httpResp.data)
//            } else {
//                com.yunnext.bluetooth.sample.domain.HDResult.Fail(ApiException(msg = httpResp.msg.ifEmpty { "登录失败！" }))
//            }
//        } catch (e: Exception) {
//            com.yunnext.bluetooth.sample.domain.HDResult.Fail(ApiException(msg = "http请求错误 ${e.localizedMessage}"))
//        }
//    }
//
//    override suspend fun logout(token: String): com.yunnext.bluetooth.sample.domain.HDResult<Boolean> {
//        return try {
//            val httpResp = apiService.logout(token = token)
//            if (httpResp.success) {
//                com.yunnext.bluetooth.sample.domain.HDResult.Success(true)
//            } else {
//                com.yunnext.bluetooth.sample.domain.HDResult.Fail(ApiException(msg = httpResp.msg.ifEmpty { "退出登录失败！" }))
//            }
//        } catch (e: Exception) {
//            com.yunnext.bluetooth.sample.domain.HDResult.Fail(ApiException(msg = "http请求错误 ${e.localizedMessage}"))
//        }
//    }
//
//    override suspend fun series(token: String): com.yunnext.bluetooth.sample.domain.HDResult<List<Product>> {
//        return try {
//            val httpResp = apiService.series(token = token)
//            if (httpResp.success) {
//                com.yunnext.bluetooth.sample.domain.HDResult.Success(httpResp.data.map {
//                    Product(
//                        code = it.code ?: throw IllegalStateException("code is null"),
//                        name = it.name ?: ""
//                    )
//                })
//            } else {
//                com.yunnext.bluetooth.sample.domain.HDResult.Fail(ApiException(msg = httpResp.msg.ifEmpty { "series失败！" }))
//            }
//        } catch (e: Exception) {
//            com.yunnext.bluetooth.sample.domain.HDResult.Fail(ApiException(msg = "http请求错误 ${e.localizedMessage}"))
//        }
//    }
//
//    override suspend fun models(token: String, seriesId: String): com.yunnext.bluetooth.sample.domain.HDResult<List<ProductModel>> {
//        return try {
//            val httpResp = apiService.models(token = token, seriesId = seriesId)
//            if (httpResp.success) {
//                com.yunnext.bluetooth.sample.domain.HDResult.Success(httpResp.data.map {
//                    ProductModel(
//                        id = it.id ?: throw IllegalStateException("id is null"),
//                        identifier = it.identifier
//                            ?: throw IllegalStateException("identifier is null"),
//                        img = it.img ?: "",
//                        name = it.name ?: ""
//                    )
//                })
//            } else {
//                com.yunnext.bluetooth.sample.domain.HDResult.Fail(ApiException(msg = httpResp.msg.ifEmpty { "models失败！" }))
//            }
//        } catch (e: Exception) {
//            com.yunnext.bluetooth.sample.domain.HDResult.Fail(ApiException(msg = "http请求错误 ${e.localizedMessage}"))
//        }
//    }
//
//    override suspend fun check(token: String, code: String): com.yunnext.bluetooth.sample.domain.HDResult<ScanResultVo> {
//        return try {
//            val httpResp = apiService.check(token = token, code = code)
//            if (httpResp.success) {
//                com.yunnext.bluetooth.sample.domain.HDResult.Success(
//                    ScanResultVo(
//                        peiJianCode = httpResp.data.componentCode ?: "",
//                        wuLiuCode = httpResp.data.code ?: code,
//                        productCode = httpResp.data.productCode?:""
//                    )
//                )
//            } else {
//                com.yunnext.bluetooth.sample.domain.HDResult.Fail(ApiException(msg = httpResp.msg.ifEmpty { "检查失败！" }))
//            }
//        } catch (e: Exception) {
//            com.yunnext.bluetooth.sample.domain.HDResult.Fail(ApiException(msg = "http请求错误 ${e.localizedMessage}"))
//        }
//    }
//
//    override suspend fun finish(
//        token: String,
//        code: String,
//        componentCode: String,
//        productCode: String,
//        result: String
//    ): com.yunnext.bluetooth.sample.domain.HDResult<Boolean> {
//        return try {
//            val map = mapOf(
//                "code" to code,
//                "componentCode" to componentCode,
//                "productCode" to productCode,
//                "result" to result,
//            )
//            val json = Gson().toJson(map)
//             val body = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
////            val body = FormBody.Builder()
////                .add("code", code)
////                .add("componentCode", componentCode)
////                .add("result", result)
////                .build()
//            val httpResp =
////                apiService.finish(token = token, code = code, componentCode, productCode, result)
//                apiService.finish(token = token, body = body)
//            if (httpResp.success) {
//                com.yunnext.bluetooth.sample.domain.HDResult.Success(true)
//            } else {
//                com.yunnext.bluetooth.sample.domain.HDResult.Fail(ApiException(msg = httpResp.msg.ifEmpty { "完成失败！" }))
//            }
//        } catch (e: Exception) {
//            com.yunnext.bluetooth.sample.domain.HDResult.Fail(ApiException(msg = "http请求错误 ${e.localizedMessage}"))
//        }
//    }
//
//}