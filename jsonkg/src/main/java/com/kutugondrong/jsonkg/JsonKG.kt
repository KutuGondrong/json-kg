package com.kutugondrong.jsonkg

import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

/**
 * KG KutuGondrong
 * This is the main class for using JsonParser. JsonParser is default convert json for this module
 */
class JsonKG {

    /**
     * Parse json and returns the json as object dynamic.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> fromJson(json: String, classOf: KClass<*>, isArray: Boolean = false): T {
        return fromJsonMainFunction(json, classOf, isArray)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> fromJson(jsonStream: InputStream, classOf: KClass<*>, isArray: Boolean = false): T {
        val writer: Writer = StringWriter()
        val buffer = CharArray(1024)
        jsonStream.use { value ->
            val reader: Reader = BufferedReader(InputStreamReader(value, "UTF-8"))
            var n: Int
            while (reader.read(buffer).also { n = it } != -1) {
                writer.write(buffer, 0, n)
            }
        }

        val jsonString: String = writer.toString()
        return fromJsonMainFunction(jsonString, classOf, isArray)
    }

    /**
     * Parse object dynamic and returns to Json
     */

    @Suppress("UNCHECKED_CAST")
    fun <T : Any>toJson(value: T) : String {
        return toJsonMainFunction(value)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> fromJsonMainFunction(json: String, classOf: KClass<*>, isArray: Boolean): T {
        try {
            if (isArray) {
                val list = ArrayList<Any>()
                JSONArray(json).also {
                    for (i in 0 until it.length()) {
                        val jsonObject = it.getJSONObject(i)
                        val reflectionDuplicate = processFromJson(classOf.java, jsonObject)
                        reflectionDuplicate.also { duplicate -> list.add(duplicate) }
                    }
                }
                return list as T
            }
            val jsonObject = JSONObject(json)
            return processFromJson(classOf.java, jsonObject) as T
        } catch (e: Exception){
            throw JsonParserException(e.toString())
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun  processFromJson(classOf: Class<*>, jsonObject: JSONObject): Any {
        val myPackage = classOf.`package`
        val result = instanceConstructor(classOf)
        val fields = result::class.java.declaredFields
        for(field in fields) {
            field.isAccessible = true
            var duplicateJsonObject = jsonObject
            /**
             * To Serialize use SerializedName
             * @see SerializedName
             */
            val serializedName = field.getAnnotation(SerializedName::class.java)
            var entity = serializedName?.value ?: field.name
            /**
             * To special Serialize use SpecialSerializedName
             * @see SpecialSerializedName
             */
            val specialSerializedName = field.getAnnotation(SpecialSerializedName::class.java)
            specialSerializedName?.also {
                when {
                    it.values.isNotEmpty() -> {
                        it.values.last().also { value ->
                            entity = value
                        }
                        duplicateJsonObject = getSpecialJsonObject(it.values, jsonObject)
                    }
                    else -> {
                        throw JsonParserException("" +
                                "com.kutugondrong.jsonkg.SpecialSerializedName " +
                                "is empty")
                    }
                }
            }
            if (serializedName !=null && !serializedName.serialize) continue
            when (field.type as Class<*>) {
                String::class.java -> {
                    duplicateJsonObject.optString(entity).also {
                        field.set(result, it)
                    }
                }
                Integer::class.java -> {
                    duplicateJsonObject.optInt(entity).also {
                        field.set(result, it)
                    }
                }
                Int::class.java -> {
                    duplicateJsonObject.optInt(entity).also {
                        field.set(result, it)
                    }
                }
                Boolean::class.java -> {
                    duplicateJsonObject.optBoolean(entity).also {
                        field.set(result, it)
                    }
                }
                Float::class.java -> {
                    duplicateJsonObject.optDouble(entity).also {
                        if (!it.isNaN()) {
                            field.set(result, it.toFloat())
                        }
                    }
                }
                Double::class.java -> {
                    duplicateJsonObject.optDouble(entity).also {
                        if (!it.isNaN()) {
                            field.set(result, it)
                        }
                    }
                }
                Long::class.java -> {
                    duplicateJsonObject.optLong(entity).also {
                        field.set(result, it)
                    }
                }
                ArrayList::class.java -> {
                    val fieldListType: ParameterizedType =
                        field.genericType as ParameterizedType
                    val genericClass = fieldListType.actualTypeArguments[0] as Class<*>
                    val list = ArrayList<Any>()
                    val jsonArray = duplicateJsonObject.optJSONArray(entity)
                    jsonArray?.also {
                        for (i in 0 until jsonArray.length()) {
                            val jsonObjectDuplicate = jsonArray.getJSONObject(i)
                            jsonObjectDuplicate?.also {
                                val reflectionDuplicate = processFromJson(genericClass, it)
                                reflectionDuplicate.also { value -> list.add(value) }
                                field.set(result, list)
                            }
                        }
                    }
                }
                List::class.java -> {
                    val fieldListType: ParameterizedType =
                        field.genericType as ParameterizedType
                    val genericClass = fieldListType.actualTypeArguments[0] as Class<*>
                    val list = ArrayList<Any>()
                    val jsonArray = duplicateJsonObject.optJSONArray(entity)
                    jsonArray?.also {
                        for (i in 0 until jsonArray.length()) {
                            val jsonObjectDuplicate = jsonArray.getJSONObject(i)
                            jsonObjectDuplicate?.also {
                                val reflectionDuplicate = processFromJson(genericClass, it)
                                reflectionDuplicate.also { duplicate -> list.add(duplicate) }
                                field.set(result, list)
                            }
                        }
                    }
                }
                else -> {
                    if (myPackage?.name == field.type.`package`?.name) {
                        val myClass = Class.forName(field.type.name) as Class<*>
                        val jsonObjectDuplicate: JSONObject? = duplicateJsonObject.optJSONObject(entity)
                        jsonObjectDuplicate?.also {
                            val reflectionDuplicate = processFromJson(myClass, it)
                            field.set(result, reflectionDuplicate)
                        }
                    }
                }
            }
        }
        return result
    }

    private fun getSpecialJsonObject(
        values: Array<out String>,
        jsonObject: JSONObject,
    ): JSONObject {
        if (values.size == 1) {
            return jsonObject
        }
        var o: JSONObject? = jsonObject
        for (i in 0 until values.size - 1) {
            o = o?.optJSONObject(values[i])
        }
        return o ?: jsonObject
    }

    private fun instanceConstructor(
        classOf: Class<*>,
    ): Any {
        if (classOf.constructors.isEmpty()) {
            return classOf.newInstance()
        }
        val c = classOf.constructors.first()
        val arguments = c.parameterTypes
            .map {
                when(it) {
                    List::class.java -> ArrayList<Any>()
                    ArrayList::class.java -> ArrayList<Any>()
                    Int::class.java -> 0
                    Long::class.java -> 0
                    Double::class.java -> 0
                    Boolean::class.java -> false
                    Float::class.java -> 0F
                    Char::class.java -> 0.toChar()
                    String::class.java -> ""
                    is Class<*> -> instanceConstructor(it)
                    else -> null
                }
            }
            .toTypedArray()

        return c.newInstance(*arguments)
    }

    private fun <T : Any>toJsonMainFunction(value: T) : String {
        var result = ""
        var isList = false
        when (value) {
            is ArrayList<*> -> {
                isList = true
                val lastIndex = value.size - 1
                value.forEachIndexed { index, it ->
                    result += toJsonMainFunction(it)
                    if (index != lastIndex) {
                        result += ", "
                    }
                }
            }
            is List<*> -> {
                isList = true
                val lastIndex = value.size - 1
                value.forEachIndexed { index, it ->
                    result += toJsonMainFunction(it as Any)
                    if (index != lastIndex) {
                        result += ", "
                    }
                }
            }
            else -> {
                val myPackage = value.javaClass.`package`
                val lastIndex = value::class.memberProperties.size - 1
                value::class.memberProperties.forEachIndexed { index, it ->
                    if (it.visibility == KVisibility.PUBLIC) {
                        val cloneValue = it.getter.call(value)
                        var name = "\"${it.name}\": "
                        it.javaField?.getAnnotation(SerializedName::class.java)?.also {
                            name = "\"${it.value}\": "
                        }

                        val resultValue = if (cloneValue?.javaClass?.`package` == myPackage) {
                            toJsonMainFunction(cloneValue as Any)
                        } else {
                            when (cloneValue) {
                                is String -> {
                                    "\"$cloneValue\""
                                }
                                is Double -> {
                                    "\"$cloneValue\""
                                }
                                is Float -> {
                                    "\"$cloneValue\""
                                }
                                is ArrayList<*> -> {
                                    toJsonMainFunction(cloneValue as Any)
                                }
                                is List<*> -> {
                                    toJsonMainFunction(cloneValue as Any)
                                }
                                else -> {
                                    "$cloneValue"
                                }
                            }
                        }
                        val specialSerializedName = it.javaField?.getAnnotation(
                            SpecialSerializedName::class.java)
                        when {
                            specialSerializedName == null -> {
                                result += name+resultValue
                            }
                            specialSerializedName.values.size == 1 -> {
                                result += "\"${specialSerializedName.values.first()}\": "+resultValue
                            }
                            specialSerializedName.values.size > 1 -> {
                                specialSerializedName.also {
                                    var resultSpecial = "\"${it.values.last()}\": $resultValue"
                                    for (i in it.values.size - 2 downTo 0) {
                                        resultSpecial= "\"${it.values[i]}\": {$resultSpecial}"
                                    }
                                    result += resultSpecial
                                }
                            }
                            else  -> {
                                result += name+resultValue
                            }
                        }
                        if (index != lastIndex) {
                            result += ", "
                        }
                    }
                }
                result = "{$result}"
            }
        }
        if (isList) {
            result = "[$result]"
        }
        return result
    }

}