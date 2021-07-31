package com.kutugondrong.jsonkg

import org.junit.Assert
import org.junit.Test

/**
 * Unit test for JsonParser
 * @see JsonKG
 */

class JsonKGTest {

    private val exampleJsonObject = "{\n" +
            "\t\"id\": 206,\n" +
            "\t\"name\": \"Hedy Simamora\"\n" +
            "}"

    private val exampleJsonArray =
        "[{ \"id\": 206, \"name\": \"Hedy Simamora\" }, { \"id\": 201, \"name\": \"Hedy Simamora\" }]"

    class User {
        var id: Int? = null
        var name: String? = null
    }

    @Test
    fun `Test Success JsonParser from json to object`() {
        val jsonParser = JsonKG()
        val result = jsonParser.fromJson(exampleJsonObject, User::class) as User
        Assert.assertEquals(result.id, 206)
        Assert.assertEquals(result.name, "Hedy Simamora")
        val jsonString = jsonParser.toJson(result)
        Assert.assertEquals(jsonString.filterNot {it.isWhitespace() }, exampleJsonObject.filterNot {it.isWhitespace() })
    }

    @Test
    fun `Test Failed JsonParser from json to object`() {
        val jsonParser = JsonKG()
        val result = jsonParser.fromJson(
            "{}",
            User::class,
        ) as User
        Assert.assertNotEquals(result.id, 206)
        Assert.assertNotEquals(result.name, "Hedy Simamora")
    }

    @Test
    fun `Test Success JsonParser from json to Array`() {
        val jsonParser = JsonKG()
        val result = jsonParser.fromJson(exampleJsonArray, User::class, true) as ArrayList<User>
        Assert.assertEquals(result.size > 0, true)
    }

    @Test
    fun `Test Failed JsonParser from json to Array`() {
        val jsonParser = JsonKG()
        val result = jsonParser.fromJson(
            "[]",
            User::class, true
        ) as ArrayList<User>
        Assert.assertNotEquals(result.size > 0, true)
    }
}