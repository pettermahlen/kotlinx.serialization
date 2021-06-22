package kotlinx.benchmarks.json

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.benchmarks.model.MacroTwitterFeed
import kotlinx.benchmarks.model.MicroTwitterFeed
import kotlinx.serialization.json.*
import org.openjdk.jmh.annotations.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.TimeUnit
import kotlin.io.path.deleteIfExists
import kotlin.io.path.outputStream

@Warmup(iterations = 7, time = 1)
@Measurement(iterations = 7, time = 1)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Fork(2)
open class TwitterFeedBenchmark {

    /*
        * Macro feed benchmark with a lot of UTF-16 used to track general regressions.
        *
        * This is a small piece of twitter feed taken from one of the simdjson repository
        * with Kotlin classes generated by Json2Kotlin plugin (and also manually adjusted)
        */
    val resource = TwitterFeedBenchmark::class.java.getResource("/twitter_macro.json")!!
//    private val input = resource.readBytes().decodeToString()
//    private val twitter = Json.decodeFromString(MacroTwitterFeed.serializer(), input)
    private val jsonNoAltNames = Json { useAlternativeNames = false }
    private val jsonIgnoreUnknwn = Json { ignoreUnknownKeys = true }
    private val jsonIgnoreUnknwnNoAltNames = Json { ignoreUnknownKeys = true; useAlternativeNames = false}

    private var file: Path? = null

    @Setup
    fun init() {
//        require(twitter == Json.decodeFromString(MacroTwitterFeed.serializer(), Json.encodeToString(MacroTwitterFeed.serializer(), twitter)))
//        file = Files.createTempFile("json_benchmark", "tmp")
    }

    @TearDown
    fun tearDown() {
        file?.deleteIfExists()
    }

    // Order of magnitude: ~400 op/s
//    @Benchmark
//    fun decodeTwitter() = Json.decodeFromString(MacroTwitterFeed.serializer(), input)
//
//    @Benchmark
//    fun decodeTwitterNoAltNames() = jsonNoAltNames.decodeFromString(MacroTwitterFeed.serializer(), input)
//
//    @Benchmark
//    fun encodeTwitter() = Json.encodeToString(MacroTwitterFeed.serializer(), twitter)
//
//    @Benchmark
//    fun encodeTwitterWriteText() {
//        file?.outputStream()?.use {
//            it.bufferedWriter().write(Json.encodeToString(MacroTwitterFeed.serializer(), twitter))
//        }
//    }
//
//    @Benchmark
//    fun encodeTwitterByteArrayStream(): String {
//        return ByteArrayOutputStream().also { Json.encodeToStream(MacroTwitterFeed.serializer(), twitter, it) }.toString()
//    }
//
//    @Benchmark
//    fun encodeTwitterWriteStream() {
//        file?.outputStream()?.use {
//            Json.encodeToStream(MacroTwitterFeed.serializer(), twitter, it)
//        }
//    }
//
//    @Benchmark
//    fun encodeTwitterJacksonStream() {
//        file?.outputStream()?.use {
//            objectMapper.writeValue(it, twitter)
//        }
//    }

//    @Benchmark
//    fun decodeMicroTwitter() = jsonIgnoreUnknwn.decodeFromString(MicroTwitterFeed.serializer(), input)

    @Benchmark
    fun decodeMicroTwitterReadText(): MicroTwitterFeed {
        return resource.openStream().use {
            jsonIgnoreUnknwn.decodeFromString(MicroTwitterFeed.serializer(), it.bufferedReader().readText())
        }
    }

//    val byteInput = input.encodeToByteArray()

//    @Benchmark
//    fun decodeMicroTwitterByteArrayStream(): MicroTwitterFeed {
//        return jsonIgnoreUnknwn.decodeFromStream(MicroTwitterFeed.serializer(), ByteArrayInputStream(byteInput))
//    }

    @Benchmark
    fun decodeMicroTwitterStream(): MicroTwitterFeed {
        return resource.openStream().use {
            jsonIgnoreUnknwnNoAltNames.decodeFromStream(MicroTwitterFeed.serializer(), it.buffered(16 * DEFAULT_BUFFER_SIZE + 1024))
        }
    }

    @Benchmark
    fun decodeMicroTwitterJacksonStream(): MicroTwitterFeed {
        return resource.openStream().use {
            objectMapper.readValue(it, MicroTwitterFeed::class.java)
        }
    }

    private val objectMapper: ObjectMapper =
        jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

//    @Benchmark
//    fun decodeMicroTwitterNoAltNames() = jsonIgnoreUnknwnNoAltNames.decodeFromString(MicroTwitterFeed.serializer(), input)

}
