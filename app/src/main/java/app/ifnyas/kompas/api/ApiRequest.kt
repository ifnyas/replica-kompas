package app.ifnyas.kompas.api

import androidx.appcompat.app.AlertDialog
import app.ifnyas.kompas.App.Companion.cxt
import app.ifnyas.kompas.api.ApiClient.httpClient
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject

class ApiRequest {

    private val TAG by lazy { javaClass.simpleName }

    private fun exHandler(e: Throwable) {
        AlertDialog.Builder(cxt)
                .setTitle("Exception")
                .setMessage(e.message)
                .show()
    }

    // Request
    companion object {
        const val POST = "POST"
        const val PUT = "PUT"
        const val GET = "GET"
    }

    private suspend fun request(
            method: String, path: String, body: JsonObject,
            params: List<Pair<String, String>>
    ): HttpResponse {
        return try {
            when (method) {
                POST -> httpClient.post(path = path, body = body)
                PUT -> httpClient.put(path = path, body = body)
                GET -> httpClient.get(path = path) {
                    params.forEach { parameter(it.first, it.second) }
                }
                else -> throw Exception()
            }
        } catch (e: Exception) {
            // print log
            e.printStackTrace()

            // create exception dialog
            withContext(Dispatchers.Main) { exHandler(e) }

            // return res
            (e as ClientRequestException).response
        }
    }

    // Client - GET
    suspend fun getProfile(search: String, page: Int, limit: Int): HttpResponse {
        val method = GET
        val path = "external/find.php"
        val params = listOf(
                Pair("command", search),
                Pair("page", "$page"),
                Pair("limit", "$limit")
        )
        val body = buildJsonObject {}
        return request(method, path, body, params)
    }

    suspend fun getArticle(guid: String): HttpResponse {
        val method = GET
        val path = "2016/detail.php"
        val params = listOf(
                Pair("guid", guid)
        )
        val body = buildJsonObject {}
        return request(method, path, body, params)
    }
    /*
    {
  "status": true,
  "result": {
    "parent_name": "regional",
    "lipsus": "",
    "kanal": "regional",
    "siteno": 78,
    "suptitle": "",
    "title": "Cerita Karyawan Curi Uang Rp 340 Juta dan Emas 177 Gram Milik Majikan,untuk Beli Motor RX King hingga Airsoft Gun",
    "keyword": ", , , ",
    "description": "Kayawan perusahaan di Bandung mencuri uang Rp 450 juta dan emas 177 gram milik majikannya untuk membeli motor RX King dan airsoft gun.",
    "urlpage": "http://bandung.kompas.com/read/2021/03/09/121200078/cerita-karyawan-curi-uang-rp-340-juta-dan-emas-177-gram-milik-majikan-untuk",
    "urlshort": "http://bandung.kompas.com/read/2021/03/09/121200078/cerita-karyawan-curi-uang-rp-340-juta-dan-emas-1",
    "subcategory": "Bandung",
    "tag": "bandung",
    "link": "http://bandung.kompas.com/read/2021/03/09/121200078/cerita-karyawan-curi-uang-rp-340-juta-dan-emas-177-gram-milik-majikan-untuk",
    "date": "2021-03-09 12:12:00",
    "embed": [],
    "photoblock": [
      {
        "block": "https://asset.kompas.com/crops/WapG2MFtBmt6oR00rJx-NiTVZfY=/104x41:904x574/750x500/data/photo/2018/03/29/914302588.jpg",
        "author": "SHUTTERSTOCK",
        "caption": "Ilustrasi pencuri",
        "orderid": "1"
      }
    ],
    "videoblock": [],
    "content": [
      "<p><strong>KOMPAS.com</strong> - N (35) seorang karyawan di salah satu perusahaan di Bandung mencuri Rp 450 juta dan emas 177 gram milik majikannya.</p>",
      "<p>Ia melakukan aksinya bersama kerabatnya berinisial S (35) pada Senin (1/2/2021).</p>",
      "[ads]",
      "<p>Uang hasil pencurian digunakan untuk foya-foya seperti membeli motor dan <em>airsoft gun.</em></p>",
      "<p>\"Hasil kejahatan dibelikan barang berharga seperti tiga unit motor RX king dan airsoftgun dua unit,\" jelas Kepala Satuan Reserse Kriminal Polrestabes Bandung AKBP Adanan Mangopang, Senin (8/3/2021).</p>",
      "<p><strong>Baca juga: <a href=\"http://regional.kompas.com/read/2021/03/08/191000278/karyawan-curi-uang-rp-450-juta-dan-emas-177-gram-majikan-untuk-foya-foya\" target=\"_self\">Karyawan Curi Uang Rp 450 Juta dan Emas 177 Gram Majikan untuk Foya-foya, Beli Motor hingga Airsoft Gun</a></strong></p>",
      "<p><h2>Congkel jendela dan masuk ke dalam rumah</h2>\nAdanan mengatakan kedua pelaku masuk ke rumah yang menjadi kantor perusahaan. Mereka lalu mencongkel jendela dan masuk ke dalam rumah.</p>",
      "<p>Sebagai karyawan korban, N memahami seluk beluk posisi barang berharga milik bosnya. Sementara S adalah eksekutor.</p>",
      "<p>Adanan mengatakan dua pelaku tersebut sudah tiga kali melakukan pencurian.</p>",
      "<p><strong>Baca juga: <a href=\"http://denpasar.kompas.com/read/2021/03/08/175723678/kronologi-oknum-polisi-diduga-curi-cincin-emas-yang-diletakkan-di-etalase\" target=\"_self\">Kronologi Oknum Polisi Diduga Curi Cincin Emas yang Diletakkan di Etalase Toko</a></strong></p>",
      "<p>\"Tersangka sudah tahu posisi barang berharga korban,\" ucap dia. \"Yang bersangkutan melakukan pencurian itu sebanyak tiga kali,\" tambahnya.</p>",
      "<p>Sejumlah barang berharga berhasil digasak mulai dari emas ratusan gram hingga uang ratusan juta.</p>",
      "<p>\"Total kerugian 177 gram emas dalam bentuk logam mulia kemudian uang Rp 450 juta,\" katanya.</p>",
      "<p><strong>Baca juga: <a href=\"http://regional.kompas.com/read/2021/03/08/161418778/terlilit-utang-bripda-pm-nekat-curi-cincin-emas-yang-diletakkan-di-etalase\" target=\"_self\">Terlilit Utang, Bripda PM Nekat Curi Cincin Emas yang Diletakkan di Etalase Toko</a></strong></p>",
      "<p><h2>Amankan motor hingga logam mulia</h2>\nSetelah mendapatkan laporan, polisi berhasil mengamankan kedua pelaku yang masih memiliki hubungan keluarga tersebut.</p>",
      "<p>Dari tangan mereka, polisi mengamankan sejumlah barang bukti yakni tiga unit sepeda motor, dua pucuk senjata jenis airsoft gun, tiga buah rekening, hingga logam mulia dengan berat 10 gram.</p>",
      "<p>Akibat perbuatannya, dua pelaku disangkakan Pasal 363 ke 3e dan 5e KUHPidana juncto Pasal 56 KUHPidana dan diancam pidana kurungan maksimal 6 tahun.</p>",
      "<p><strong>SUMBER: KOMPAS.com (Penulis: Agie Permadi | Editor : Aprillia Ika)</strong></p>",
      "<p></p>"
    ],
    "author": {
      "id": 0,
      "name": "",
      "email": "",
      "thumb": "",
      "teaser": "",
      "jobtitle": "",
      "profile": ""
    },
    "publishedby": "Rachmawati",
    "source": null,
    "allowcomment": 1,
    "related": [
      {
        "guid": ".xml.2021.03.08.191000278",
        "title": "Karyawan Curi Uang Rp 450 Juta dan Emas 177 Gram Majikan untuk Foya-foya, Beli Motor hingga \"Airsoft Gun\"",
        "photo": "https://asset.kompas.com/crops/9KQqHWgBi6UsxBaZQ-L6pIMp1O8=/0x0:0x0/390x195/data/photo/2021/02/01/60181a229364a.jpg",
        "thumb": "https://asset.kompas.com/crops/CmweE-xvZbhiR5v1k3ZUBpXKmv0=/0x0:0x0/195x98/data/photo/2021/02/01/60181a229364a.jpg",
        "link": "http://regional.kompas.com/read/2021/03/08/191000278/karyawan-curi-uang-rp-450-juta-dan-emas-177-gram-majikan-untuk-foya-foya"
      },
      {
        "guid": ".xml.2021.03.08.175723678",
        "title": "Kronologi Oknum Polisi Diduga Curi Cincin Emas yang Diletakkan di Etalase Toko",
        "photo": "https://asset.kompas.com/crops/9wWhbMztEWWW6nlcXPfnv2jA79w=/102x0:687x390/390x195/data/photo/2013/03/21/1203039-ilustrasi-pencuri-maling-780x390.jpg",
        "thumb": "https://asset.kompas.com/crops/Zp5Rn3KIP0IRSnFS9Bz3_mWVULI=/102x0:687x390/195x98/data/photo/2013/03/21/1203039-ilustrasi-pencuri-maling-780x390.jpg",
        "link": "http://denpasar.kompas.com/read/2021/03/08/175723678/kronologi-oknum-polisi-diduga-curi-cincin-emas-yang-diletakkan-di-etalase"
      },
      {
        "guid": ".xml.2021.03.08.161418778",
        "title": "Terlilit Utang, Bripda PM Nekat Curi Cincin Emas yang Diletakkan di Etalase Toko",
        "photo": "https://asset.kompas.com/crops/Df4G3YSeqpr5VmXDD-vjVFd5-ow=/175x0:760x390/390x195/data/photo/2013/08/31/1533508IMG-7490780x390.JPG",
        "thumb": "https://asset.kompas.com/crops/Ed0qFgiUs0Y4FiimU-tOUWlv48U=/175x0:760x390/195x98/data/photo/2013/08/31/1533508IMG-7490780x390.JPG",
        "link": "http://regional.kompas.com/read/2021/03/08/161418778/terlilit-utang-bripda-pm-nekat-curi-cincin-emas-yang-diletakkan-di-etalase"
      },
      {
        "guid": ".xml.2021.03.04.15500051",
        "title": "Pelaku Pembunuhan Sembunyi 8 Jam Dalam Toko di Blitar, Curi Uang dan Aniaya Pemilik hingga Tewas, Ini Ceritanya",
        "photo": "https://asset.kompas.com/crops/zw9nAocWCjt8lje2w4UqT96Bvb0=/0x0:0x0/390x195/data/photo/2021/03/03/603f044843de9.jpg",
        "thumb": "https://asset.kompas.com/crops/dEDrU7pU0Cvsqw1v2FskEiSU8I8=/0x0:0x0/195x98/data/photo/2021/03/03/603f044843de9.jpg",
        "link": "http://regional.kompas.com/read/2021/03/04/15500051/pelaku-pembunuhan-sembunyi-8-jam-dalam-toko-di-blitar-curi-uang-dan-aniaya"
      },
      {
        "guid": ".xml.2021.03.03.135348878",
        "title": "\"Dia Curi HP Milik Saya, Akhirnya Kita Aniaya...\" ",
        "photo": "https://asset.kompas.com/crops/HJIv-lJm4f7UxYGDxyRRJbHjNSk=/0x0:0x0/390x195/data/photo/2021/03/03/603ef8173bacf.jpg",
        "thumb": "https://asset.kompas.com/crops/GGqDhled7eBULVlb7SiXKgyu5Bo=/0x0:0x0/195x98/data/photo/2021/03/03/603ef8173bacf.jpg",
        "link": "http://regional.kompas.com/read/2021/03/03/135348878/dia-curi-hp-milik-saya-akhirnya-kita-aniaya"
      }
    ]
  }
}
     */
}