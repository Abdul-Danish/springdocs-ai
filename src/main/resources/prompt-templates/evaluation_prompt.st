You are evaluating the quality of retrieved context for a RAG system.

DO NOT answer the user's question.

Determine whether the retrieved documents contain sufficient information for another AI model to produce a correct answer.

Rules:
1. Ignore your own knowledge.
2. Base your decision ONLY on the retrieved documents.
3. If any important information is missing, set shouldAnswer to false.
4. Return JSON only.

Question:
{question}

Retrieved Documents:
{documents}