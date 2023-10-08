package com.example.ir

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import kotlin.math.ln

class Indexer(private val invertedIndex: Map<String, List<String>>) {

    fun getTermFrequency(term: String, document: String): Int {
        val documents = invertedIndex[term.toLowerCase()] ?: return 0
        return documents.count { it.equals(document, ignoreCase = true) }
    }

    fun getTotalTermsInDoc(document: String): Int {
        return invertedIndex.values.flatten().count { it.equals(document, ignoreCase = true) }
    }
}


class MainActivity : AppCompatActivity() {
//    private val tokenizer: TokenizerME
//    private val nameFinder: NameFinderME

    private val K = 5
    private lateinit var filesearch: Button
    private lateinit var keyword: Button
    private lateinit var rank: Button
    private lateinit var prm: Button
    private lateinit var non: Button
    private lateinit var next: Button
    private lateinit var move: Button
    private lateinit var field: EditText
    private lateinit var resultTextView: TextView
    private lateinit var pnm : Button
    private val invertedIndex: MutableMap<String, MutableList<String>> = mutableMapOf()
    private lateinit var python: Python
    private lateinit var script: PyObject

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        field = findViewById(R.id.field)
        filesearch = findViewById(R.id.button)
        keyword = findViewById(R.id.button2)
        rank = findViewById(R.id.button3)
        prm = findViewById(R.id.pr)
        non = findViewById(R.id.non)
        next = findViewById(R.id.next)
        move = findViewById(R.id.move)
        pnm =findViewById(R.id.pnm)
        resultTextView = findViewById(R.id.tv)
        resultTextView.setMovementMethod(ScrollingMovementMethod())

// Assuming you have a function to build the inverted index
        var invertedIndex = buildInvertedIndex(this)
        val index = Indexer(invertedIndex)

        filesearch.setOnClickListener {
            resultTextView.text = ""
 //           search(this, invertedIndex)

        }
        keyword.setOnClickListener {
            // search(this)
            resultTextView.text = ""
            //searchInAssets(this)
      //      searchAndRank(this, invertedIndex)
        }
        rank.setOnClickListener {
            resultTextView.text = ""
     //       rankDocumentsByTFIDF(this, invertedIndex)
            //var terms = preprocessQuery()
        }
        prm.setOnClickListener {
            resultTextView.text = ""

      //      prm(this, invertedIndex)
        }
        non.setOnClickListener {
            resultTextView.text = ""

      //      rankDocumentsByNLM(this, invertedIndex)
        }
        pnm.setOnClickListener {
    //        rankDocumentsByPNM(this, invertedIndex)
        }
        next.setOnClickListener {
            resultTextView.text = ""
            invertedIndex.forEach { (term, indices) ->
                val text = "$term: ${indices.joinToString(", ")}"
                resultTextView.append("$text\n")
            }
      //   resultTextView.text = invertedIndex.toString()
        }
        move.setOnClickListener {
            val intent = Intent(this, newactivity::class.java)
            startActivity(intent)
        }
    }


    fun preprocessTextFiles(context: Context): List<String> {
        val preprocessedTextList = mutableListOf<String>()
        try {
            val fileNames = context.assets.list("") ?: emptyArray()

            fileNames.forEach { fileName ->
                if (!fileName.startsWith("webkit") && !fileName.startsWith("images") && fileName.endsWith(".txt")) {
                    val preprocessedText = preprocessTextFile(context, fileName)
                    preprocessedTextList.add(preprocessedText)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return preprocessedTextList
    }

//    private fun preprocessTextFile(context: Context, fileName: String): String {
//        val inputStream = context.assets.open(fileName)
//        val reader = BufferedReader(InputStreamReader(inputStream))
//
//        val words = reader.readLines().flatMap { it.split("\\s+".toRegex()) }
//        val preprocessedWords = words.map { preprocessQuery(it) }
//
//        val preprocessedText = preprocessedWords.joinToString(" ")
//
//        reader.close()
//        inputStream.close()
//
//        return preprocessedText
//    }

    private fun preprocessTextFile(context: Context, fileName: String): String {
        var text :String = ""
        if (! Python.isStarted()) {
            Python.start( AndroidPlatform(this));
            Toast.makeText(this, "Python Started", Toast.LENGTH_SHORT).show()
        }
        val python = Python.getInstance()
        Toast.makeText(this, "Instance Created", Toast.LENGTH_SHORT).show()

/// idher crash kr rhi ap
// p  ,, runkro acha   bcz script mai abhi bhi erros hain
        
        val script = python.getModule("script")

        //idhr log lagana sai se
        Log.e("Script Error", "$(e.message)")
        Toast.makeText(this, "Script Entered", Toast.LENGTH_SHORT).show()

        try {
            Toast.makeText(this, "Entered", Toast.LENGTH_SHORT).show()


            Log.d("PythonModule", "Python module 'script' loaded successfully.") // Log statement
            Toast.makeText(this, "Script", Toast.LENGTH_SHORT).show()


            val inputStream = context.assets.open(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream))
            Toast.makeText(this, "File Read", Toast.LENGTH_SHORT).show()

            val words = reader.readLines().flatMap { it.split("\\s+".toRegex()) }
            val preprocessedWords = words.map { script.callAttr("factoria", it).toString() }
            Toast.makeText(this, "Passed", Toast.LENGTH_SHORT).show()
            val preprocessedText = preprocessedWords.joinToString(" ")
            Toast.makeText(this, "Joined", Toast.LENGTH_SHORT).show()

            reader.close()
            inputStream.close()
            text = preprocessedText

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return text
    }

    fun buildInvertedIndex(context: Context): Map<String, List<String>> {
        val invertedIndex = mutableMapOf<String, MutableList<String>>()

        try {
            val fileNames = context.assets.list("") // List of files in the assets folder

            fileNames?.forEachIndexed { idx, fileName ->
                if (!fileName.startsWith("webkit") && !fileName.startsWith("images") && fileName.endsWith(".txt")) {
                    val preprocessedText = preprocessTextFile(context, fileName)
                    val terms = preprocessedText.split("\\s+".toRegex())

                    terms.forEach { term ->
                        invertedIndex.getOrPut(term) { mutableListOf() }
                            .add(fileName)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return invertedIndex
    }


//    fun buildInvertedIndex(context: Context): Map<String, List<String>> {
//        val preprocessedTextList: List<String> = preprocessTextFiles(this)
//        val invertedIndex = mutableMapOf<String, MutableList<String>>()
//
//        preprocessedTextList.forEachIndexed { idx, preprocessedText ->
//            val terms = preprocessedText.split("\\s+".toRegex())
//            terms.forEach { term ->
//                invertedIndex.getOrPut(term) { mutableListOf() }
//                    .add(fileNames[idx])
//            }
//        }
//
//        return invertedIndex
//    }















//    private fun buildInvertedIndex(context: Context): Map<String, List<String>> {
//        val invertedIndex = mutableMapOf<String, MutableList<String>>()
//
//        try {
//            val fileNames = context.assets.list("") // List of files in the assets folder
//
//            fileNames?.forEach { fileName ->
//                if (!fileName.startsWith("webkit") && !fileName.startsWith("images") && fileName.endsWith(".txt")) {
//                    val preprocessedText = preprocessTextFile(context, fileName)
//                    val terms = preprocessedText.split("\\s+".toRegex())
//
//                    terms.forEach { term ->
//                        invertedIndex.getOrPut(term.toLowerCase()) { mutableListOf() }
//                            .add(fileName)
//                    }
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//        return invertedIndex
//    }
//
//    private fun preprocessTextFile(context: Context, fileName: String): String {
//        if (!Python.isStarted()) {
//            Python.start(AndroidPlatform(context))
//        }
//
//        val python = Python.getInstance()
//        val script = python.getModule("script")
//
//        val inputStream = context.assets.open(fileName)
//        val reader = BufferedReader(InputStreamReader(inputStream))
//
//        val words = reader.readLines().flatMap { it.split("\\s+".toRegex()) }
//        val preprocessedWords = words.map { script.callAttr("preprocess_query", it).toString() }
//
//        val preprocessedText = preprocessedWords.joinToString(" ")
//
//        reader.close()
//        inputStream.close()
//
//        return preprocessedText
//    }


//    fun preprocessTextFiles(context: Context): List<String> {
//        if (!Python.isStarted()) {
//            Python.start(AndroidPlatform(context))
//        }
//
//        val python = Python.getInstance()
//        val script = python.getModule("script")
//
//        val fileNames = context.assets.list("") // List of files in the assets folder
//
//        val preprocessedTexts = mutableListOf<String>()
//
//        fileNames?.forEach { fileName ->
//            if (fileName.endsWith(".txt")) {
//                val inputStream = context.assets.open(fileName)
//                val reader = BufferedReader(InputStreamReader(inputStream))
//
//                val words = reader.readLines().flatMap { it.split("\\s+".toRegex()) }
//               val preprocessedWords = words.map { script.callAttr("preprocess_query", it).toString() }
////                val preprocessedWords = Python.getInstance().getModule("script").callAttr("preprocess_query").toString()
//
//                val preprocessedText = preprocessedWords.joinToString(" ")
//
//                preprocessedTexts.add(preprocessedText)
//
//                reader.close()
//                inputStream.close()
//            }
//        }
//
//        return preprocessedTexts
//    }
//
//    private fun buildInvertedIndex(context: Context): Map<String, List<String>> {
//        val invertedIndex = mutableMapOf<String, MutableList<String>>()
//
//        try {
//            val preprocessedTexts = preprocessTextFiles(context)
//
//            preprocessedTexts.forEachIndexed { index, preprocessedText ->
//                val fileName = "text_file_$index.txt" // Replace with actual file name if available
//                val terms = preprocessedText.split("\\s+".toRegex())
//
//                terms.forEach { term ->
//                    invertedIndex.getOrPut(term.toLowerCase()) { mutableListOf() }
//                        .add(fileName)
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//        return invertedIndex
//    }

//    private fun buildInvertedIndex(context: Context): Map<String, List<String>> {
//        val invertedIndex = mutableMapOf<String, MutableList<String>>()
//
//        try {
//            val assets = context.assets.list("") // Get a list of files in the assets folder
//
//            assets?.filter { it != "images" && it != "webkit" }?.forEach { fileName ->
//                context.assets.open(fileName).use { inputStream ->
//                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
//                        val content = reader.readText()
//                        val terms = content.split("\\s+".toRegex())
//                        terms.forEach { term ->
//                            invertedIndex.getOrPut(term.toLowerCase()) { mutableListOf() }
//                                .add(fileName)
//                        }
//                    }
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//        return invertedIndex
//    }
    fun search(context: Context, invertedIndex: Map<String, List<String>>) {
       val query = field.text.toString()
        val stringsToSearch: List<String> = preprocess(query)
        val processedFiles = HashSet<String>() // Keep track of processed files

        stringsToSearch.forEach { content ->
            val lowercaseContent = content.toLowerCase()

            invertedIndex.forEach { (query, matchingFiles) ->
                if (lowercaseContent.contains(query)) {
                    matchingFiles.forEach { fileName ->
                        if (fileName != "webkit" && fileName != "images" && !processedFiles.contains(fileName)) {
                            try {
                                context.assets.open(fileName).use { inputStream ->
                                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                                        val fileContent = reader.readText()
                                        val index = fileContent.indexOf(query)
                                        resultTextView.append("Filename: $fileName\n, Content: $fileContent\n, Index: $index\n")
                                        // Mark the file as processed
                                        processedFiles.add(fileName)
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        }
    }
    fun preprocess(text: String): List<String> {
        // Define a list of stop words
        val stopWords = listOf("i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours", "yourself", "yourselves", "he", "him", "his", "himself", "she", "her", "hers", "herself", "it", "its", "itself", "they", "them", "their", "theirs", "themselves", "what", "which", "who", "whom", "this", "that", "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having", "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while", "of", "at", "by", "for", "with", "about", "against", "between", "into", "through", "during", "before", "after", "above", "below", "to", "from", "up", "down", "in", "out", "on", "off", "over", "under", "again", "further", "then", "once")

        // Tokenize the input text
        val tokens = text.split(Regex("\\W+"))

        // Remove stop words and apply preprocessing steps
        val preprocessedTokens = tokens
            .filter { it !in stopWords }
            .map { it.toLowerCase().trim() }

        return preprocessedTokens
    }


    ////////////////////////////////////PERFECT INDEXED CODE ASSIGNMENT 1////////////////////////////////////////////////////////////////
//    fun search(context: Context, invertedIndex: Map<String, List<String>>) {
//        val query = field.text.toString()
//
//        // Convert the query to lowercase for case-insensitive search
//        val lowercaseQuery = query.toLowerCase()
//
//        // Check if the query term is in the inverted index
//        if (invertedIndex.containsKey(lowercaseQuery)) {
//            val matchingFiles = invertedIndex[lowercaseQuery]!!
//
//            val processedFiles = HashSet<String>() // Keep track of processed files
//
//            matchingFiles.forEach { fileName ->
//                if (fileName != "webkit" && fileName != "images" && !processedFiles.contains(fileName)) {
//                    try {
//                        context.assets.open(fileName).use { inputStream ->
//                            BufferedReader(InputStreamReader(inputStream)).use { reader ->
//                                val content = reader.readText()
//                                val index = content.indexOf(lowercaseQuery)
//                                resultTextView.append("Filename: $fileName\n, Content: $content\n, Index: $index\n")
//                                // Mark the file as processed
//                                processedFiles.add(fileName)
//                            }
//                        }
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                }
//            }
//        }
//    }
    fun searchAndRank(context: Context, invertedIndex: Map<String, List<String>> ) {
        val query = field.text.toString()
        val stringsToSearch: List<String> = preprocess(query)
        val processedFiles = HashSet<String>() // Keep track of processed files

        stringsToSearch.forEach { content ->
            val lowercaseContent = content.toLowerCase()

            invertedIndex.forEach { (query, matchingFiles) ->
                if (lowercaseContent.contains(query)) {
                    matchingFiles.forEach { fileName ->
                        if (fileName != "webkit" && fileName != "images" && !processedFiles.contains(fileName)) {
                            try {
                                context.assets.open(fileName).use { inputStream ->
                                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                                        val fileContent = reader.readText()
                                        val occurrences = fileContent.split(Regex("\\W+"))
                                            .withIndex()
                                            .filter { it.value.toLowerCase() == query }
                                            .map { it.index }

                                        if (occurrences.isNotEmpty()) {
                                            val indexes = occurrences.joinToString(", ") // Join the indexes for display
                                            resultTextView.append("Filename: $fileName\n, Content: $fileContent\n, Occurrences: ${occurrences.size}, Indexes: $indexes\n")
                                            // Mark the file as processed
                                            processedFiles.add(fileName)
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        }
    }


    //////////////////////////////PERFECT INDEXED ASSIGNMENT 2//////////////////////////////////////
//    fun searchAndRank(context: Context, invertedIndex: Map<String, List<String>>) {
//            val query = field.text.toString()
//
//            // Convert the query to lowercase for case-insensitive search
//            val lowercaseQuery = query.toLowerCase()
//
//            // Check if the query term is in the inverted index
//            if (invertedIndex.containsKey(lowercaseQuery)) {
//                val matchingFiles = invertedIndex[lowercaseQuery]!!
//
//                val fileOccurrencesMap = HashMap<String, List<Int>>() // Map to store occurrences and indexes
//
//                matchingFiles.forEach { fileName ->
//                    if (fileName != "webkit" && fileName != "images") {
//                        try {
//                            context.assets.open(fileName).use { inputStream ->
//                                BufferedReader(InputStreamReader(inputStream)).use { reader ->
//                                    val content = reader.readText()
//                                    val occurrences = content.split(Regex("\\W+"))
//                                        .withIndex()
//                                        .filter { it.value.toLowerCase() == lowercaseQuery }
//                                        .map { it.index }
//
//                                    if (occurrences.isNotEmpty()) {
//                                        fileOccurrencesMap[fileName] = occurrences
//                                    }
//                                }
//                            }
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                        }
//                    }
//                }
//
//                // Sort files by occurrences in descending order
//                val sortedFiles = fileOccurrencesMap.entries.sortedByDescending { it.value.size }
//
//                // Display results in the resultTextView
//                sortedFiles.forEachIndexed { index, entry ->
//                    val indexes = entry.value.joinToString(", ") // Join the indexes for display
//                    resultTextView.append("Rank ${index + 1}: Filename: ${entry.key}, Occurrences: ${entry.value.size}, Indexes: $indexes\n")
//                }
//            }
//        }



    ////////////////////////// CORRECT WORKING BEFORE INDEXING///////////////////////////////////////////////
//    private fun search(context: Context) {
//        var text = field.text.toString()
//        val assetManager = context.assets
//        val fileList = mutableListOf<String>()
//        //val filelist: Array<String> = assetManager.list("")
//        if (text.isNotBlank()){
//
//            try {
//            val files = assetManager.list("") // List all files in the assets folder
//            if (files != null) {
//                for (file in files) {
//                    if (file.contains(text)) {
//                        fileList.add(file)
//                    }
//                }
//            }
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//        if (fileList.isEmpty()) {
//            resultTextView.text = "No files found"
//        } else {
//            resultTextView.text = fileList.joinToString("\n")
//        }
//    }
//        else{
//            resultTextView.text = "Enter text first!!"
//        }
//    }
//    }

////////////////////////// CORRECT WORKING BEFORE INDEXING///////////////////////////////////////////////

//            fun searchInAssets(context: Context) {
//        val text = field.text.toString()
//
//        if (text.isNotBlank()) {
//            val fileList = mutableListOf<Pair<String, Int>>()
//            try {
//                val assets = context.assets.list("") // Get a list of files in the assets folder
//
//                assets?.filter { it != "images" && it != "webkit" }?.forEach { fileName ->
//                    context.assets.open(fileName).use { inputStream ->
//                        BufferedReader(InputStreamReader(inputStream)).use { reader ->
//                            val content = StringBuilder()
//                            var line: String?
//                            while (reader.readLine().also { line = it } != null) {
//                                content.append(line).append('\n')
//                            }
//                            val regex = Regex("\\b${Regex.escape(text)}\\b", RegexOption.IGNORE_CASE)
//                            val matchCount = regex.findAll(content).count()
//                            if (matchCount > 0) {
//                                fileList.add(Pair(fileName, matchCount))
//                            }
//                        }
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//            val rankedDocuments = fileList.sortedByDescending { it.second }
//            displayRankedDocuments(rankedDocuments)
//        } else {
//            resultTextView.text = "Enter text first!!"
//        }
//    }
//        fun calculateTFIDF(
//            termFrequency: Int,
//            totalTermsInDoc: Int,
//            totalDocs: Int,
//            docsWithTerm: Int
//        ): Double {
//            val tf = termFrequency.toDouble() / totalTermsInDoc
//            val idf =
//                ln(totalDocs.toDouble() / (docsWithTerm + 1)) // Using ln for natural logarithm (base 'e')
//            return tf * idf
//        }
//
//        fun rankDocumentsByTFIDF(
//            invertedIndex: Map<String, List<String>>,
//            index: Indexer
//        ): List<Pair<String, Double>> {
//            val query = field.text.toString() // Make sure to convert it to a String
//            val queryTerms = query.trim().split("\\s+".toRegex()) // Split the query into terms
//            val fileList = mutableListOf<Pair<String, Double>>()
//
//            if (field.text.isNotBlank()) {
////            val fileList = mutableListOf<Pair<String, Double>>()
//
//                queryTerms.forEach { term ->
//                    invertedIndex[term.toLowerCase()]?.let { docList ->
//                        docList.forEach { doc ->
//                            val termFrequency =
//                                index.getTermFrequency(
//                                    term,
//                                    doc
//                                ) // Use indexer to get term frequency
//                            val totalTermsInDoc =
//                                index.getTotalTermsInDoc(doc) // Use indexer to get total terms in doc
//                            val tfidf = calculateTFIDF(
//                                termFrequency,
//                                totalTermsInDoc,
//                                invertedIndex.size,
//                                docList.size
//                            )
//                            fileList.add(Pair(doc, tfidf))
//                        }
//                    }
//                    val rankedDocuments = fileList.sortedByDescending { it.second }
//                    displayRankedDocumentsTFIDF(rankedDocuments)
//                }
//            } else {
//                resultTextView.text = "Enter text first!!"
//            }
//            return fileList
//        }
//

    // Function to calculate TF-IDF
    fun calculateTFIDF(termFrequency: Int, totalTermsInDoc: Int, totalDocs: Int, docsWithTerm: Int): Double {
        val tf = termFrequency.toDouble() / totalTermsInDoc
        val idf = ln(totalDocs.toDouble() / (docsWithTerm + 1))
        return tf * idf
    }

    // Function to rank documents by TF-IDF using inverted index
    @RequiresApi(Build.VERSION_CODES.N)
    fun rankDocumentsByTFIDF(context: Context, invertedIndex: Map<String, List<String>>) {
        val query = field.text.toString()
        val inputList = preprocess(query)
        val preprocessedTokensList = inputList.map { preprocess(it) }.flatten()

        val totalDocs = invertedIndex.values.flatten().distinct().size

        val fileScores = mutableMapOf<String, Double>()

        preprocessedTokensList.forEach { term ->
            if (invertedIndex.containsKey(term)) {
                val matchingFiles = invertedIndex[term]!!

                matchingFiles.forEach { fileName ->
                    if (fileName != "webkit" && fileName != "images") {
                        try {
                            context.assets.open(fileName).use { inputStream ->
                                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                                    val content = reader.readText()
                                    val preprocessedTokens = preprocess(content)
                                    val docsWithTerm = invertedIndex[term]?.size ?: 0

                                    val tfidfScore = preprocessedTokens.sumByDouble { token ->
                                        val termFrequency = preprocessedTokens.count { it == token }
                                        calculateTFIDF(termFrequency, preprocessedTokens.size, totalDocs, docsWithTerm)
                                    }

                                    fileScores[fileName] = fileScores.getOrDefault(fileName, 0.0) + tfidfScore
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

        if (fileScores.isNotEmpty()) {
            val rankedDocuments = fileScores.entries.sortedByDescending { it.value }
            displayRankedDocumentsTFIDF(rankedDocuments)
        } else {
            resultTextView.text = "No matching documents found"
        }
    }


    // Function to display ranked documents by TF-IDF
    fun displayRankedDocumentsTFIDF(rankedDocuments: List<Map.Entry<String, Double>>) {
        if (rankedDocuments.isNotEmpty()) {
            rankedDocuments.forEachIndexed { index, entry ->
                val formattedScore = String.format("%.3f", entry.value)
                resultTextView.append("Rank ${index + 1}: Filename: ${entry.key}, TF-IDF Score: $formattedScore\n")
            }
        } else {
            resultTextView.text = "No matching documents found"
        }
    }


////////////////////////// CORRECT WORKING BEFORE INDEXING///////////////////////////////////////////////
//fun calculateTFIDF(termFrequency: Int, totalTermsInDoc: Int, totalDocs: Int, docsWithTerm: Int): Double {
//    val tf = termFrequency.toDouble() / totalTermsInDoc
//    val idf = ln(totalDocs.toDouble() / (docsWithTerm + 1)) // Using ln for natural logarithm (base 'e')
//    return tf * idf
//}
//                fun rankDocumentsByTFIDF( context: Context) {
//        var query = field.text
//        var text = query.trim()
//
//
//        if (text.isNotBlank()) {
//            val fileList = mutableListOf<Pair<String, Double>>() // Using Pair to store file name and TF-IDF score
//            try {
//                val assets = context.assets.list("") // Get a list of files in the assets folder
//                val totalDocs = assets?.size ?: 0 // Total number of documents
//
//                assets?.filter { it != "images" && it != "webkit" }?.forEach { fileName ->
//                    context.assets.open(fileName).use { inputStream ->
//                        BufferedReader(InputStreamReader(inputStream)).use { reader ->
//                            val content = StringBuilder()
//                            var line: String?
//                            while (reader.readLine().also { line = it } != null) {
//                                content.append(line).append('\n')
//                            }
//
//                            // Tokenize the document content and query
//                            val documentTokens = content.split("\\s+".toRegex())
//                            val queryTokens = text.split("\\s+".toRegex())
//
//                            // Calculate TF-IDF scores for query terms and sum them up
//                            val queryTFIDFScore = queryTokens.sumByDouble { term ->
//                                val termFrequency = documentTokens.count { it.equals(term, ignoreCase = true) }
//                                calculateTFIDF(termFrequency, documentTokens.size, totalDocs, 1) // Assuming the query is like a separate document
//                            }
//
//                            // Calculate TF-IDF scores for document terms and sum them up
//                            val documentTFIDFScore = documentTokens.sumByDouble { term ->
//                                val termFrequency = documentTokens.count { it.equals(term, ignoreCase = true) }
//                                calculateTFIDF(termFrequency, documentTokens.size, totalDocs, 1)
//                            }
//
//                            fileList.add(Pair(fileName, queryTFIDFScore * documentTFIDFScore))
//                        }
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//
//            val rankedDocuments = fileList.sortedByDescending { it.second }
//            displayRankedDocumentsTFIDF(rankedDocuments)
//        } else {
//            resultTextView.text = "Enter text first!!"
//        }
//    }
//        fun displayRankedDocumentsTFIDF(rankedDocuments: List<Pair<String, Double>>) {
//            if (rankedDocuments.isNotEmpty()) {
//                rankedDocuments.forEachIndexed { index, (fileName, tfidfScore) ->
//                    val formattedScore = String.format("%.3f", tfidfScore)
//                    resultTextView.append("Rank ${index + 1}: $fileName - TF-IDF Score: $formattedScore\n\n")
//                }
//            } else {
//                resultTextView.text = "No matching documents found"
//            }
//        }
@RequiresApi(Build.VERSION_CODES.N)
fun prm(context: Context, invertedIndex: Map<String, List<String>>) {
    if(field.text.isNotBlank()){
        val k1 = 1.2
        val b = 0.75
        val k2 = 100.0

        val query = field.text.toString()
        val inputList = preprocess(query)
        val avgDocLength = invertedIndex.values.flatten().map { fileName ->
            try {
                context.assets.open(fileName).use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        reader.readText().split(" ").size
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                0
            }
        }.average()

        val fileScores = mutableMapOf<String, Double>()

        inputList.forEach { term ->
            if (invertedIndex.containsKey(term)) {
                val matchingFiles = invertedIndex[term]!!

                matchingFiles.forEach { fileName ->
                    if (fileName != "webkit" && fileName != "images") {
                        try {
                            context.assets.open(fileName).use { inputStream ->
                                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                                    val content = reader.readText()
                                    val preprocessedTokens = preprocess(content)
                                    val docLength = preprocessedTokens.size
                                    val docsWithTerm = invertedIndex[term]?.size ?: 0

                                    //   val idf = kotlin.math.log((totalDocs - docsWithTerm + 0.5) / (docsWithTerm + 0.5))
                                    val totalDocs = invertedIndex.values.flatten().distinct().size
                                    val idf = ln(totalDocs.toDouble() / (docsWithTerm + 1))
                                    val tf = preprocessedTokens.count { it == term }
                                    val tfComponent = ((k1 + 1.0) * tf) / (k1 * ((1 - b) + b * (docLength / avgDocLength)) + tf)

                                    val userQueryTermFreq = inputList.count { it == term }
                                    val userQueryComponent = ((k2 + 1.0) * userQueryTermFreq) / (k2 + userQueryTermFreq)

                                    val bm25Score = idf * tfComponent * userQueryComponent

                                    fileScores[fileName] = fileScores.getOrDefault(fileName, 0.0) + bm25Score
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

        if (fileScores.isNotEmpty()) {
            val rankedDocuments = fileScores.entries.sortedByDescending { it.value }
            displayRankedDocumentsBM25(rankedDocuments)
        } else {
            resultTextView.text = "No matching documents found"
        }
    }
    else{
        resultTextView.text="Enter text first"
    }

}
    fun displayRankedDocumentsBM25(rankedDocuments: List<Map.Entry<String, Double>>) {
        val stringBuilder = StringBuilder()

        for ((index, entry) in rankedDocuments.withIndex()) {
            val fileName = entry.key
            val score = entry.value

            stringBuilder.append("Rank: ${index + 1}\n")
            stringBuilder.append("File Name: $fileName\n")
            stringBuilder.append("Score: $score\n\n")
        }

        resultTextView.text = stringBuilder.toString()
    }
    @RequiresApi(Build.VERSION_CODES.N)
    fun rankDocumentsByNLM(context: Context, invertedIndex: Map<String, List<String>>) {
        val k1 = 1.2
        val b = 0.75
        val k2 = 100.0

        val query = field.text.toString()
        val inputList = preprocess(query)
        val avgDocLength = invertedIndex.values.flatten().map { fileName ->
            try {
                context.assets.open(fileName).use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        reader.readText().split(" ").size
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                0
            }
        }.average()

        val totalDocs = invertedIndex.values.flatten().distinct().size

        val fileScores = mutableMapOf<String, Double>()

        inputList.forEach { term ->
            if (invertedIndex.containsKey(term)) {
                val matchingFiles = invertedIndex[term]!!

                matchingFiles.forEach { fileName ->
                    if (fileName != "webkit" && fileName != "images") {
                        try {
                            context.assets.open(fileName).use { inputStream ->
                                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                                    val content = reader.readText()
                                    val preprocessedTokens = preprocess(content)
                                    val docLength = preprocessedTokens.size

                                    val docsWithTerm = invertedIndex[term]?.size ?: 0
                                    val idf = ln(totalDocs.toDouble() / (docsWithTerm + 1))

                                    val tf = preprocessedTokens.count { it == term }
                                    val tfComponent = ((k1 + 1.0) * tf) / (k1 * ((1 - b) + b * (docLength / avgDocLength)) + tf)

                                    val userQueryTermFreq = inputList.count { it == term }
                                    val userQueryComponent = ((k2 + 1.0) * userQueryTermFreq) / (k2 + userQueryTermFreq)

                                    val nlmScore = idf * tfComponent * userQueryComponent

                                    fileScores[fileName] = fileScores.getOrDefault(fileName, 0.0) + nlmScore
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

        if (fileScores.isNotEmpty()) {
            val rankedDocuments = fileScores.entries.sortedByDescending { it.value }
            displayRankedDocumentsNLM(rankedDocuments, resultTextView)
        } else {
            resultTextView.text = "No matching documents found"
        }
    }
    fun displayRankedDocumentsNLM(rankedDocuments: List<Map.Entry<String, Double>>, resultTextView: TextView) {
        val stringBuilder = StringBuilder()

        for ((index, entry) in rankedDocuments.withIndex()) {
            val fileName = entry.key
            val score = entry.value

            stringBuilder.append("Rank: ${index + 1}\n")
            stringBuilder.append("File Name: $fileName\n")
            stringBuilder.append("Score: $score\n\n")
        }

        resultTextView.text = stringBuilder.toString()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun rankDocumentsByPNM(context: Context, invertedIndex: Map<String, List<String>>) {
        val k1 = 1.2
        val b = 0.75
        val k2 = 100.0
        val mu = 2000.0

        val query = field.text.toString()
        val inputList = preprocess(query)
        val avgDocLength = invertedIndex.values.flatten().map { fileName ->
            try {
                context.assets.open(fileName).use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        reader.readText().split(" ").size
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                0
            }
        }.average()

        val totalDocs = invertedIndex.values.flatten().distinct().size

        val fileScores = mutableMapOf<String, Double>()

        inputList.forEachIndexed { index, term ->
            if (invertedIndex.containsKey(term)) {
                val matchingFiles = invertedIndex[term]!!

                matchingFiles.forEach { fileName ->
                    if (fileName != "webkit" && fileName != "images") {
                        try {
                            context.assets.open(fileName).use { inputStream ->
                                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                                    val content = reader.readText()
                                    val preprocessedTokens = preprocess(content)
                                    val docLength = preprocessedTokens.size
                                    val docsWithTerm = invertedIndex[term]?.size ?: 0

                                    val idf = ln(totalDocs.toDouble() / (docsWithTerm + 1))

                                    val tf = preprocessedTokens.count { it == term }
                                    val tfComponent = ((k1 + 1.0) * tf) / (k1 * ((1 - b) + b * (docLength / avgDocLength)) + tf)

                                    val userQueryTermFreq = inputList.count { it == term }
                                    val userQueryComponent = ((k2 + 1.0) * userQueryTermFreq) / (k2 + userQueryTermFreq)

                                    val positionalScore = calculatePositionalScore(preprocessedTokens, term, index, mu)

                                    val pnmScore = idf * tfComponent * userQueryComponent * positionalScore

                                    fileScores[fileName] = fileScores.getOrDefault(fileName, 0.0) + pnmScore
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

        if (fileScores.isNotEmpty()) {
            val rankedDocuments = fileScores.entries.sortedByDescending { it.value }
            displayRankedDocumentsPNM(rankedDocuments, resultTextView)
        } else {
            resultTextView.text = "No matching documents found"
        }
    }

    fun calculatePositionalScore(tokens: List<String>, term: String, queryPosition: Int, mu: Double): Double {
        val termPositions = tokens.indices.filter { tokens[it] == term }
        val proximityScore = termPositions.map { kotlin.math.exp(-mu * kotlin.math.abs(it - queryPosition)) }.sum()
        return proximityScore
    }
    fun displayRankedDocumentsPNM(rankedDocuments: List<Map.Entry<String, Double>>, resultTextView: TextView) {
        val stringBuilder = StringBuilder()

        for ((index, entry) in rankedDocuments.withIndex()) {
            val fileName = entry.key
            val score = entry.value

            stringBuilder.append("Rank: ${index + 1}\n")
            stringBuilder.append("File Name: $fileName\n")
            stringBuilder.append("Score: $score\n\n")
        }

        resultTextView.text = stringBuilder.toString()
    }



//        private fun nonOverlappedListModel(context: Context) {
//            // Step 1: Identify Terms of Interest (already done in preprocessQuery)
//            val userInput = field.text.toString()
//            val terms = userInput.split("\\s+".toRegex())
//
//            // Step 2: Retrieve Documents per Term
//            val documentLists = mutableListOf<List<String>>()
//
//            for (term in terms) {
//                val documentTerms = retrieveDocumentsForTerm(context, term)
//                documentLists.add(documentTerms)
//            }
//
//            // Step 3: Combine Lists for Non-Overlapping Results
//            val nonOverlapDocuments = combineDocumentLists(documentLists)
//
//            // Step 4: Present Results
//            displayNonOverlappingDocuments(nonOverlapDocuments)
//        }
//
//        private fun retrieveDocumentsForTerm(context: Context, term: String): List<String> {
//            val documentTermsList = mutableListOf<String>()
//
//            try {
//                val assets = context.assets.list("") ?: return emptyList()
//
//                for (fileName in assets) {
//                    context.assets.open(fileName).use { inputStream ->
//                        BufferedReader(InputStreamReader(inputStream)).use { reader ->
//                            val content = reader.readText()
//                            if (term in content) {
//                                documentTermsList.add(fileName)
//                            }
//                        }
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//
//            return documentTermsList
//        }
//
//        private fun combineDocumentLists(documentLists: List<List<String>>): Set<String> {
//            return documentLists.flatten().toSet()
//        }
//
//        private fun displayNonOverlappingDocuments(nonOverlapDocuments: Set<String>) {
//            resultTextView.text = ""
//            if (nonOverlapDocuments.isNotEmpty()) {
//                nonOverlapDocuments.forEachIndexed { index, fileName ->
//                    resultTextView.append("Rank ${index + 1}: $fileName\n")
//                }
//            }
//        }

//    private fun nonOverlappedListModel(context: Context) {
//        // Step 1: Identify Terms of Interest (already done in preprocessQuery)
//
//        // Step 2: Retrieve Documents per Term
//        val userInput = field.text.toString()
//        val terms = userInput.split("\\s+".toRegex())
//        val documentLists = mutableListOf<List<String>>()
//
////        for (term in terms) {
////            val documentTerms = retrieveDocumentsForTerm(context)
////            documentLists.add(documentTerms)
////        }
//        for (term in terms) {
//            val documentTerms = retrieveDocumentsForTerm(context, term)
//            documentLists.add(documentTerms)
//        }
//
//
//        // Step 3: Combine Lists for Non-Overlapping Results
//        val nonOverlapDocuments = combineDocumentLists(documentLists)
//
//        // Step 4: Present Results
//        displayNonOverlappingDocuments(nonOverlapDocuments)
//    }
//
//    private fun retrieveDocumentsForTerm(context: Context, term:String): List<String> {
//        val documentTermsList = mutableListOf<String>()
//      //  var term = field.text.toString()
//        try {
//            val assets = context.assets.list("") ?: return emptyList()
//
//            assets.filter { it != "images" && it != "webkit" }.forEach { fileName ->
//                context.assets.open(fileName).use { inputStream ->
//                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
//                        val content = reader.readText()
//                        if (term in content) {
//                            documentTermsList.add(fileName)
//                        } else {
//                          //  Toast.makeText(context, "Term not found in content: $term", Toast.LENGTH_SHORT).show()
//                            println("Term not found in content: $term")
//                        }
//
//                    }
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//        return documentTermsList
//    }
//
//    private fun combineDocumentLists(documentLists: List<List<String>>): Set<String> {
//        return documentLists.flatten().toSet()
//    }
//
//    private fun displayNonOverlappingDocuments(nonOverlapDocuments: Set<String>) {
//        resultTextView.text = ""
//        if (nonOverlapDocuments.isNotEmpty()) {
//            nonOverlapDocuments.forEachIndexed { index, fileName ->
//                resultTextView.append("Rank ${index + 1}: $fileName\n")
//            }
//        }
//    }
//
//    fun identifyProximalNodes(userQuery: String): List<String> {
//        val tokens = tokenizer.tokenize(userQuery)
//
//        // Find names (you can customize the model based on your specific needs)
//        val nameSpans: Array<Span> = nameFinder.find(tokens)
//
//        val proximalNodes = nameSpans.map { span ->
//            tokens.sliceArray(span.start until span.end).joinToString(" ")
//        }
//
//        return proximalNodes
//    }
    }


