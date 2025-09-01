// ✅ Responses API 요청 DTO (템플릿 사용)
data class ResponsesRequest(
    val model: String = "gpt-5-nano",
    val prompt: PromptRef = PromptRef(),   // 템플릿 참조
    val max_output_tokens: Int = 1024
)

// ✅ 템플릿 참조 (대시보드 Prompt ID)
data class PromptRef(
    val id: String = "pmpt_68b55f84d4c08190858e719b32ca2a0c0b080deca7eb79c8",  // 대시보드에서 발급된 Prompt ID
    val version: String = "18",  // 특정 버전에 고정하려면 버전 문자열로
    val variables: Map<String, String>? = null
)

// ✅ Responses API 응답 DTO (간단형)
data class ResponsesChoice(val content: String?)
data class ResponsesOutput(val text: String?, val choices: List<ResponsesChoice>?)
// ✅ 응답 DTO (Responses API 응답 구조)
data class ResponsesResponse(
    val id: String?,
    val output: List<OutputMessage>?
)

data class OutputMessage(
    val id: String?,
    val type: String?,              // "message"
    val role: String?,              // "assistant"
    val content: List<OutputContent>?
)

data class OutputContent(
    val type: String?,              // "output_text"
    val text: String?               // 실제 답변 텍스트
)