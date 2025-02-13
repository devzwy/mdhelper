package io.github.devzwy.mdhelper.data

open class RowBaseResult<T> {
    var rows: List<T> = arrayListOf()
    var total: Int = 0
}

open class Row {
    var rowid: String = ""
    var ctime: String = ""
    var caid: UserInfo? = null
    var ownerid: UserInfo? = null
    var utime: String = ""
    var autoid: Int = 0
    var allowdelete: Boolean = false
    var controlpermissions: String = ""
}

open class UserInfo {
    var accountId: String = ""
    var fullname: String = ""
    var avatar: String = ""
    var status: Int = 0
}

open class MdTableInfo {
    var worksheetId: String = ""
    var name: String = ""
    var views: List<MdView> = arrayListOf()
    var controls: List<MdControl> = arrayListOf()
}

open class MdView {
    var viewId: String = ""
    var name: String = ""
}

open class MdControl {
    var controlId: String = ""
    var controlName: String = ""
    var type: Int = -1
    var attribute: Int = -1
    var row: Int = -1
    var col: Int = -1
    var userPermission: Int = -1
    var size: Int = -1
    var disabled: Boolean = false
    var checked: Boolean = false
    var controlPermissions: String = ""
    var alias: String = ""
}

open class MdAppInfo {
    var projectId: String = ""
    var appId: String = ""
    var name: String = ""
    var iconUrl: String = ""
    var color: String = ""
    var desc: String = ""
    var sections: List<MdSection> = arrayListOf()
}

open class MdSection {
    var sectionId: String = ""
    var name: String = ""
    var items: List<MdSectionItem> = arrayListOf()
    var childSections: List<MdSection> = arrayListOf()
}

open class MdSectionItem {
    var id: String = ""
    var name: String = ""
    var type: Int = -1
    var iconUrl: String = ""
    var status: Int = -1
    var alias: String = ""
}