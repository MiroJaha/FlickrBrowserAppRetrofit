package com.example.flickrbrowserappretrofit

data class Data(val title: String?, val server: String?, val id: String?, val secret: String?, var checkBox: Boolean) {
    companion object{
        var search: String =""
        var count: Int =0
    }
}