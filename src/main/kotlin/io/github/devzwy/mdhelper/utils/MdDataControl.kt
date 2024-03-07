package io.github.devzwy.mdhelper.utils

import io.github.devzwy.mdhelper.data.MuliDataType
import io.github.devzwy.mdhelper.data.MuliEditType
import io.github.devzwy.mdhelper.data.OptionDataType

class MdDataControl private constructor(val controls: List<HashMap<String, Any?>>) {
    class Builder {

        private val list = arrayListOf<HashMap<String, Any?>>()


        /**
         * 添加普通的字段
         * [controlId] 键
         * [data] 值
         */
        fun addControl(controlId: String, data: String?): Builder {
            list.add(hashMapOf("controlId" to controlId, "value" to data))
            return this
        }

        /**
         * 添加附件字段
         * [controlId] 键
         * [dataList] 值,可以为多个url或多个base64，例如：["url1","url2"...]，或["base641","base642"...]
         * [fileNames] 当[dataList]的值为Base64时这里的文件名称列表长度必须和[dataList]的长度一致，填写文件的后缀，例如["1.jpg","2.pdf"...]
         * [dataType] 值的类型 使用[MuliDataType]构造，默认[MuliDataType.URL]
         * [editType] 当前字段的编辑类型，使用[MuliEditType]构造，默认[MuliEditType.REPLACE]
         */
        fun addMulti(
            controlId: String, dataList: List<String>?, fileNames: List<String>? = null, dataType: MuliDataType = MuliDataType.URL,
            editType: MuliEditType = MuliEditType.REPLACE
        ): Builder {
            val hashMap = hashMapOf<String, Any?>("controlId" to controlId, "editType" to editType.type, "valueType" to dataType.type)

            if (dataType == MuliDataType.URL) {
                //附件类型为多个url
                hashMap.put("value", dataList?.joinToString(","))
            } else if (dataType == MuliDataType.BASE64) {

                val fjList = arrayListOf<HashMap<String, Any?>>()
                if (dataList != null && fileNames != null) {
                    val combinedList = dataList.zip(fileNames).map { (data, fileName) ->
                        mapOf("baseFile" to data, "fileName" to fileName)
                    }
                    hashMap.put("controlFiles", combinedList)
                } else {
                    hashMap.put("controlFiles", fjList)
                }

            }
            list.add(hashMap)
            return this
        }


        /**
         * 添加选项字段
         * [controlId] 键
         * [data] 值
         * [dataType] 使用[OptionDataType]构造，[OptionDataType.NOT_ADD]不增加选项，[OptionDataType.ADD]允许增加选项（默认为[OptionDataType.NOT_ADD]，为[OptionDataType.NOT_ADD]时匹配不到已有选项时传入空，为[OptionDataType.ADD]时，匹配不到时会创建新选项并写入）
         */
        fun addOption(controlId: String, data: String?, dataType: OptionDataType = OptionDataType.NOT_ADD): Builder {
            list.add(hashMapOf("controlId" to controlId, "value" to data, "valueType" to dataType.type))
            return this
        }

        fun build(): MdDataControl {
            return MdDataControl(list)
        }
    }

}
