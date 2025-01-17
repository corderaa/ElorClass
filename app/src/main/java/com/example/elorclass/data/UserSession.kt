package com.example.elorclass.data

object UserSession {

    private var name: String? = null
    private var surname: String? = null
    private var id: String? = null
    private var adress: String? = null
    private var firstTelephone: String? = null
    private var secondTelephone: String? = null
    private var studies: String? = null
    private var password: String? = null
    private var schoolyear: Int? = null
    private var dual: Boolean? = null
    private var registered: Boolean? = null
    private var role: Int? = null

    fun setUserSession(
        name: String, surname: String, id: String, adress: String,
        firstTelephone: String, secondTelephone: String, studies: String,
        password: String, schoolyear: Int, dual: Boolean, registered: Boolean, role:Int
    ) {
        this.name = name
        this.surname = surname
        this.id = id
        this.adress = adress
        this.firstTelephone = firstTelephone
        this.secondTelephone = secondTelephone
        this.studies = studies
        this.password = password
        this.schoolyear = schoolyear
        this.dual = dual
        this.registered = registered
        this.role = role
    }

    fun fetchName(): String? = name
    fun fetchSurname(): String? = surname
    fun fetchId(): String? = id
    fun fetchAdress(): String? = adress
    fun fetchFirstTelephone(): String? = firstTelephone
    fun fetchSecondTelephone(): String? = secondTelephone
    fun fetchStudies(): String? = studies
    fun fetchPassword(): String? = password
    fun fetchSchoolyear(): Int? = schoolyear
    fun fetchDual(): Boolean? = dual
    fun fetchRegistered(): Boolean? = registered
    fun fetchRole(): Int? = role

    fun clearSession() {
        name = null
        surname = null
        id = null
        adress = null
        firstTelephone = null
        secondTelephone = null
        studies = null
        password = null
        schoolyear = null
        dual = null
        registered = null
        role = null
    }

    fun isUserLoggedIn(): Boolean = id != null && name != null
}
