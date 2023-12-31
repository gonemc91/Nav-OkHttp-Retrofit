package com.example.http.data.base

import com.example.http.domain.AppException
import com.example.http.domain.BackendException
import com.example.http.domain.ConnectionException
import com.example.http.domain.ParseBackendResponseException
import com.example.http.testutils.catch
import com.example.http.testutils.wellDone
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import com.squareup.moshi.Moshi
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException


@ExperimentalCoroutinesApi
class BaseRetrofitSourceTest {

    @Test
    fun getRetrofitReturnsInstanceFromConfig(){
        val expectedRetrofit = mockk<Retrofit>()
        val source = createBaseRetrofitSource(
            retrofit = expectedRetrofit
        )
        val retrofit = source.retrofit



        assertSame(expectedRetrofit, retrofit)
    }


    @Test
    fun wrapRetrofitExceptionReturnsValueGeneratedBlock() = runTest{
        val source = createBaseRetrofitSource()
        val block = createMockedBlock()
        coEvery { block() } returns "test"

        val result = source.wrapRetrofitExceptions(block)

        assertEquals("test", result)

    }




    @Test
    fun wrapRetrofitExceptionsWithAppExceptionRethrowsException() = runTest {
        val source = createBaseRetrofitSource()
        val block = createMockedBlock()
        val expectedException = AppException()
        coEvery { block() } throws expectedException

        val exception: AppException = catch{
            source.wrapRetrofitExceptions(block)
        }

        assertSame(expectedException, exception)
    }

@Test
fun wrapRetrofitExceptionWithJsonDataExceptionThrowsBackendException() = runTest {
    val source = createBaseRetrofitSource()
    val block = createMockedBlock()
    coEvery { block.invoke() } throws JsonDataException()

    catch<ParseBackendResponseException> {
        source.wrapRetrofitExceptions(block)
    }

    wellDone()
}


    @Test
    fun wrapRetrofitExceptionWithJsonEncodingExceptionThrowsParseBackendResponseException() =
        runTest {
            val source = createBaseRetrofitSource()
            val block = createMockedBlock()
            coEvery { block.invoke() } throws JsonEncodingException("Bo-o-om")

            catch<ParseBackendResponseException>{
                source.wrapRetrofitExceptions(block)
            }
            wellDone()
        }

    @Test
    fun wrapRetrofitExceptionWithIOExceptionThrowsConnectionException() = runTest {
        val source = createBaseRetrofitSource()
        val block = createMockedBlock()
        coEvery { block.invoke() } throws IOException()

        catch<ConnectionException> {
            source.wrapRetrofitExceptions(block)
        }

        wellDone()
    }

    @Test
    fun wrapRetrofitExceptionWithHttpExceptionThrowsBackendException() = runTest {
        val source = createBaseRetrofitSource()
        val block = createMockedBlock()
        val httpException = mockk<HttpException>()
        val response = mockk<Response<*>>()
        val errorBody = mockk<ResponseBody>()
        val errorJson = "{\"error\": \"Oops\"}"
        coEvery { block.invoke() } throws httpException
        every { httpException.response() } returns response
        every { httpException.code() } returns 409
        every { response.errorBody() } returns errorBody
        every { errorBody.string() } returns errorJson

        val exception: BackendException = catch {
            source.wrapRetrofitExceptions(block)
        }

        assertEquals("Oops", exception.message)
        assertEquals(409, exception.code)
    }


    private fun createMockedBlock():suspend () -> String{
        return mockk()
    }

    private fun createBaseRetrofitSource(
        retrofit: Retrofit = mockk()
    ) = BaseRetrofitSource(
        RetrofitConfig(
            retrofit= retrofit,
            moshi = Moshi.Builder().build()
        )
    )


}