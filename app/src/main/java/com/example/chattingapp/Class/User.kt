package com.example.chattingapp.Class

class User {
    private var uid:String = ""
    private var username:String = ""
    private var website:String = ""
    private var status:String = ""
    private var search:String = ""
    private var profile:String = ""
    private var linkedin:String = ""
    private var github:String = ""
    private var cover:String = ""

    constructor()
    constructor(
        uid : String ,
        username : String ,
        website : String ,
        status : String ,
        search : String ,
        profile : String ,
        linkedin : String ,
        github : String ,
        cover : String
    ) {
        this.uid = uid
        this.username = username
        this.website = website
        this.status = status
        this.search = search
        this.profile = profile
        this.linkedin = linkedin
        this.github = github
        this.cover = cover
    }

    fun setUid(uid: String) {
        this.uid = uid
    }

    fun getUid(): String {
        return uid
    }

    fun setUsername(username: String) {
        this.username = username
    }

    fun getUsername(): String {
        return username
    }

    fun setWebsite(website: String) {
        this.website = website
    }

    fun getWebsite(): String {
        return website
    }

    fun setStatus(status: String) {
        this.status = status
    }

    fun getStatus(): String {
        return status
    }

    fun setSearch(search: String) {
        this.search = search
    }

    fun getSearch(): String {
        return search
    }

    fun setProfile(profile: String) {
        this.profile = profile
    }

    fun getProfile(): String {
        return profile
    }

    fun setLinkedin(linkedin: String) {
        this.linkedin = linkedin
    }

    fun getLinkedin(): String {
        return linkedin
    }

    fun setGithub(github: String) {
        this.github = github
    }

    fun getGithub(): String {
        return github
    }

    fun setCover(cover: String) {
        this.cover = cover
    }

    fun getCover(): String {
        return cover
    }
}