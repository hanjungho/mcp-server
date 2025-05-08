package org.lucky0111.pettalkmcpserver.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lucky0111.pettalkmcpserver.domain.dto.trainer.CertificationDTO;
import org.lucky0111.pettalkmcpserver.domain.dto.trainer.TrainerDTO;
import org.lucky0111.pettalkmcpserver.domain.entity.common.Tag;
import org.lucky0111.pettalkmcpserver.repository.common.TagRepository;
import org.lucky0111.pettalkmcpserver.repository.community.PostRepository;
import org.lucky0111.pettalkmcpserver.repository.community.PostTagRepository;
import org.lucky0111.pettalkmcpserver.repository.trainer.TrainerRepository;
import org.lucky0111.pettalkmcpserver.repository.trainer.TrainerTagRepository;
import org.lucky0111.pettalkmcpserver.service.trainer.TrainerService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;
    private final PostRepository postRepository;
    private final TrainerTagRepository trainerTagRepository;
    private final TrainerRepository trainerRepository;

    //  예시
//    @Tool(name = "removeUserById", description = "사용자 ID를 기준으로 사용자를 삭제합니다. 특정 ID의 사용자를 시스템에서 완전히 제거해야 할 때 사용하세요. 이 작업은 되돌릴 수 없으니 신중하게 사용해야 합니다. 예: '아이디가 abc123인 사용자 삭제해줘', '특정 ID를 가진 계정을 제거해줘', '시스템에서 이 사용자를 완전히 제거해줘'")
//    @Transactional
//    public void deleteUserById(
//            @ToolParam(description = "삭제할 사용자의 고유 ID. 시스템이 자동으로 생성한 UUID 형식의 문자열입니다. 예: '550e8400-e29b-41d4-a716-446655440000'")
//            String id) {
//        userRepository.deleteById(id);
//    }
//    @Tool(name = "removeUserByUsername", description = "사용자 이름을 기준으로 사용자를 삭제합니다. 사용자 ID를 모르지만 이름은 알고 있을 때 계정을 제거하려는 경우 사용하세요. 이 작업은 되돌릴 수 없으니 신중하게 사용해야 합니다. 예: '홍길동 사용자 계정 삭제해줘', '특정 사용자 이름으로 등록된 계정 제거해줘', '이 사용자 이름을 가진 계정을 시스템에서 지워줘'")
//    @Transactional
//    public void deleteUserByUsername(
//            @ToolParam(description = "삭제할 사용자의 이름. 사용자가 회원가입 시 입력한 고유한 아이디입니다. 대소문자를 구분합니다. 예: 'hong123', 'admin_user'")
//            String username) {
//        userRepository.deleteByUsername(username);
//    }


//    // 사용자의 질문과 관련된 내용이 태그에 있을 경우 해당 태그를 포함한 트레이너와 게시글을 검색합니다.
//    @Tool(name = "getRelatedPosts", description = "사용자의 질문과 관련된 게시글을 검색합니다. 사용자가 질문한 내용과 관련된 태그를 기반으로 게시글을 찾습니다. 예: '강아지 훈련 방법에 대한 질문이 있어요', '고양이 행동 문제에 대한 조언을 받고 싶어요', '강아지 배변 훈련에 대한 팁을 알고 싶어요', '고양이 사료 추천해줘', '분리불안 해결 방법 알려줘', '공격성 제어 방법은?', '강아지 훈련사 추천해줘', '고양이 행동 문제 해결 방법 알려줘', '강아지 훈련 팁과 요령 공유해줘', '고양이 사료 브랜드 추천해줘'")
//    public List<Long> getRelatedPosts(
//            @ToolParam(description = "사용자가 질문한 내용에 대한 태그 리스트. 범위가 클 경우 여러 개의 태그 입력. 예: '강아지 훈련', '고양이 행동 문제', '강아지 배변 훈련', '고양이 사료', '분리불안', '공격성 제어', '강아지 훈련사 추천', '고양이 행동 문제 해결', '강아지 훈련 팁과 요령', '고양이 사료 브랜드'")
//            List<String> tags) {
//        log.info("getRelatedTrainers 호출됨: tags={}", tags);
//        // 쿼리 실행
//        List<Long> results = null;
//        try {
//            results = postTagRepository.findPostIdsByTagNames(tags);
//            log.debug("쿼리 실행 완료, 결과 처리 전: {}", results);
//        } catch (Exception e) {
//            log.error("쿼리 실행 중 오류: ", e);
//            return List.of();
//        }
//
//        if (results == null || results.isEmpty()) {
//            log.warn("검색 결과 없음: tags={}", tags);
//            return List.of(); // 빈 리스트 반환하여 NPE 방지
//        }
//
//        return results;
//    }
//
//    @Tool(name = "getRelatedTrainers", description = "사용자의 질문과 관련된 트레이너를 검색합니다. 사용자가 질문한 내용과 관련된 태그를 기반으로 트레이너를 찾습니다.  예: '강아지 훈련 방법에 대한 질문이 있어요', '고양이 행동 문제에 대한 조언을 받고 싶어요', '강아지 배변 훈련에 대한 팁을 알고 싶어요', '고양이 사료 추천해줘', '분리불안 해결 방법 알려줘', '공격성 제어 방법은?', '강아지 훈련사 추천해줘', '고양이 행동 문제 해결 방법 알려줘', '강아지 훈련 팁과 요령 공유해줘', '고양이 사료 브랜드 추천해줘'")
//    public List<UUID> getRelatedTrainers(
//            @ToolParam(description = "사용자가 질문한 내용에 대한 태그 리스트. 범위가 클 경우 여러 개의 태그 입력. 예: '강아지 훈련', '고양이 행동 문제', '강아지 배변 훈련', '고양이 사료', '분리불안', '공격성 제어', '강아지 훈련사 추천', '고양이 행동 문제 해결', '강아지 훈련 팁과 요령', '고양이 사료 브랜드'")
//            List<String> tags) {
//        log.info("getRelatedTrainers 호출됨: tags={}", tags);
//
//        // 쿼리 실행
//        List<UUID> results = null;
//        try {
//            results = trainerTagRepository.findTrainerIdsByTagNames(tags);
//            log.debug("쿼리 실행 완료, 결과 처리 전: {}", results);
//        } catch (Exception e) {
//            log.error("쿼리 실행 중 오류: ", e);
//            return List.of();
//        }
//
//        if (results == null || results.isEmpty()) {
//            log.warn("검색 결과 없음: tags={}", tags);
//            return List.of(); // 빈 리스트 반환하여 NPE 방지
//        }
//
//        return results;
//    }

//    private final TrainerTagRepository trainerTagRepository;
//    private final TagRepository tagRepository;
//    private final TrainerService trainerService;
//    private final FunctionCallbackContext functionCallbackContext;
//
//    private List<String> availableTags;
//
//    @PostConstruct
//    public void init() {
//        refreshAvailableTags();
//    }
//
//    /**
//     * 사용 가능한 태그 목록을 데이터베이스에서 새로 불러옵니다.
//     */
//    public void refreshAvailableTags() {
//        try {
//            availableTags = tagRepository.findAll().stream()
//                    .map(Tag::getTagName)
//                    .collect(Collectors.toList());
//            log.info("사용 가능한 태그 목록 갱신 완료. 총 {}개 태그", availableTags.size());
//        } catch (Exception e) {
//            log.error("태그 목록 로딩 중 오류 발생", e);
//            // 기본 태그 설정
//            availableTags = List.of(
//                    "강아지 훈련", "고양이", "고양이 행동", "공격성 제어",
//                    "배변 훈련", "분리불안", "사료 추천", "샴", "초코",
//                    "행동 문제", "훈련 팁"
//            );
//        }
//    }
//
//    private static final String TRAINER_CARD_TEMPLATE = """
//        ## 🐾 {{nickname}} 트레이너
//
//        ![트레이너 프로필]({{profileImageUrl}})
//
//        **전문 분야**: {{specializationText}}
//        **경력**: {{experienceYears}}년
//        **방문 가능 지역**: {{visitingAreas}}
//        **평점**: ⭐{{averageRating}} ({{reviewCount}}개의 리뷰)
//
//        ### 소개
//        {{introduction}}
//
//        ### 대표 경력
//        {{representativeCareer}}
//
//        ### 자격증
//        {{certifications}}
//
//        ---
//        """;
//
//    private static final String SYSTEM_PROMPT = """
//        당신은 반려동물 훈련 전문가를 찾아주는 도우미입니다.
//        사용자의 질문을 분석하고, 관련된 태그를 추출하여 해당 분야의 전문 트레이너를 찾아주세요.
//
//        다음은 현재 사용 가능한 태그 목록입니다:
//        {{availableTags}}
//
//        사용자의 질문과 가장 관련성이 높은 태그를 선택하세요.
//
//        트레이너 정보는 다음 형식의 마크다운으로 출력하세요:
//
//        ```
//        {{trainerCardTemplate}}
//        ```
//
//        트레이너를 찾을 수 없는 경우, 친절하게 다른 키워드로 검색해보라고 안내해주세요.
//        항상 트레이너 추천을 하기 전에 사용자의 질문을 간략히 요약하고 어떤 도움이 필요한지 확인하세요.
//        """;
//
//    private static final String TRAINER_LIST_INTRO_TEMPLATE = """
//        안녕하세요! 질문을 분석한 결과, **{{tagList}}** 관련 전문 트레이너들을 찾았습니다.
//
//        다음은 이 분야의 전문 트레이너 목록입니다:
//
//        ---
//        """;
//
//    /**
//     * 사용자 질문을 분석하고 태그를 추출하여 관련 트레이너를 찾아 응답합니다.
//     *
//     * @param userQuery 사용자 질문
//     * @return 트레이너 추천 응답
//     */
//    public String processUserQuery(String userQuery) {
//        log.info("사용자 질문 처리 시작: {}", userQuery);
//
//        try {
//            // LLM을 통해 태그 추출
//            List<String> tags = extractTagsFromQuery(userQuery);
//            log.info("추출된 태그: {}", tags);
//
//            if (tags.isEmpty()) {
//                return "죄송합니다. 질문에서 관련 태그를 찾을 수 없습니다. 반려동물의 특정 문제나 훈련 방법에 대해 더 자세히 질문해주시겠어요?\n\n" +
//                        "현재 지원하는 주제: " + String.join(", ", availableTags);
//            }
//
//            // 태그를 기반으로 트레이너 ID 조회
//            List<UUID> trainerIds = getRelatedTrainers(tags);
//            log.info("조회된 트레이너 수: {}", trainerIds.size());
//
//            if (trainerIds.isEmpty()) {
//                return "죄송합니다. 현재 요청하신 '" + String.join(", ", tags) + "'에 관한 전문 트레이너를 찾을 수 없습니다. 다른 키워드로 다시 검색해보시겠어요?\n\n" +
//                        "현재 지원하는 주제: " + String.join(", ", availableTags);
//            }
//
//            // 트레이너 상세 정보 조회 (최대 5명으로 제한)
//            List<TrainerDTO> trainers = trainerIds.stream()
//                    .limit(5) // 너무 많은 트레이너가 나오지 않도록 제한
//                    .map(trainerService::getTrainerById)
//                    .collect(Collectors.toList());
//
//            // 트레이너 정보를 카드 형식으로 변환
//            return formatTrainerCards(trainers, tags);
//
//        } catch (Exception e) {
//            log.error("사용자 질문 처리 중 오류 발생", e);
//            return "죄송합니다. 요청 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
//        }
//    }
//
//    /**
//     * 사용자 질문에서 관련 태그를 추출합니다.
//     *
//     * @param userQuery 사용자 질문
//     * @return 추출된 태그 목록
//     */
//    private List<String> extractTagsFromQuery(String userQuery) {
//        // 간단한 키워드 매칭 로직
//        List<String> matchedTags = new ArrayList<>();
//
//        // 사용자 질문을 소문자로 변환
//        String lowerQuery = userQuery.toLowerCase();
//
//        // 각 태그에 대해 매칭 시도
//        for (String tag : availableTags) {
//            String lowerTag = tag.toLowerCase();
//            if (lowerQuery.contains(lowerTag)) {
//                matchedTags.add(tag);
//            }
//        }
//
//        // 매칭된 태그가 없으면 일부 핵심 키워드 검색
//        if (matchedTags.isEmpty()) {
//            // 반려동물 종류
//            if (lowerQuery.contains("강아지") || lowerQuery.contains("개") || lowerQuery.contains("puppy") || lowerQuery.contains("dog")) {
//                matchedTags.add("강아지 훈련");
//            }
//            if (lowerQuery.contains("고양이") || lowerQuery.contains("냥이") || lowerQuery.contains("cat") || lowerQuery.contains("kitten")) {
//                matchedTags.add("고양이");
//                matchedTags.add("고양이 행동");
//            }
//
//            // 문제 행동
//            if (lowerQuery.contains("짖") || lowerQuery.contains("공격") || lowerQuery.contains("물") || lowerQuery.contains("aggressive")) {
//                matchedTags.add("공격성 제어");
//            }
//            if (lowerQuery.contains("화장실") || lowerQuery.contains("배변") || lowerQuery.contains("소변") || lowerQuery.contains("대변") || lowerQuery.contains("실수")) {
//                matchedTags.add("배변 훈련");
//            }
//            if (lowerQuery.contains("혼자") || lowerQuery.contains("분리") || lowerQuery.contains("불안") || lowerQuery.contains("anxiety")) {
//                matchedTags.add("분리불안");
//            }
//
//            // 일반 주제
//            if (lowerQuery.contains("먹") || lowerQuery.contains("사료") || lowerQuery.contains("간식") || lowerQuery.contains("feed") || lowerQuery.contains("food")) {
//                matchedTags.add("사료 추천");
//            }
//            if (lowerQuery.contains("훈련") || lowerQuery.contains("교육") || lowerQuery.contains("가르치") || lowerQuery.contains("train")) {
//                matchedTags.add("훈련 팁");
//            }
//        }
//
//        return matchedTags;
//    }
//
//    /**
//     * 사용자의 질문과 관련된 트레이너를 검색합니다.
//     *
//     * @param tags 사용자 질문에서 추출한 태그 목록
//     * @return 관련 트레이너 ID 목록
//     */
//    @Tool(name = "getRelatedTrainers", description = "사용자의 질문과 관련된 트레이너를 검색합니다. 사용자가 질문한 내용과 관련된 태그를 기반으로 트레이너를 찾습니다.")
//    public List<UUID> getRelatedTrainers(
//            @ToolParam(description = "사용자가 질문한 내용에 대한 태그 리스트")
//            List<String> tags) {
//        log.info("getRelatedTrainers 호출됨: tags={}", tags);
//
//        if (tags == null || tags.isEmpty()) {
//            log.warn("태그 목록이 비어있음");
//            return List.of();
//        }
//
//        // 쿼리 실행
//        List<UUID> results = null;
//        try {
//            results = trainerTagRepository.findTrainerIdsByTagNames(tags);
//            log.debug("쿼리 실행 완료, 결과 처리 전: {}", results);
//        } catch (Exception e) {
//            log.error("쿼리 실행 중 오류: ", e);
//            return List.of();
//        }
//
//        if (results == null || results.isEmpty()) {
//            log.warn("검색 결과 없음: tags={}", tags);
//            return List.of(); // 빈 리스트 반환하여 NPE 방지
//        }
//
//        return results;
//    }
//
//    /**
//     * 트레이너 정보를 카드 형식으로 포맷팅합니다.
//     *
//     * @param trainers 트레이너 목록
//     * @param tags 관련 태그 목록
//     * @return 포맷팅된 트레이너 카드 문자열
//     */
//    private String formatTrainerCards(List<TrainerDTO> trainers, List<String> tags) {
//        StringBuilder result = new StringBuilder();
//
//        // 트레이너 목록 소개 추가
//        String tagListStr = String.join(", ", tags);
//        String intro = TRAINER_LIST_INTRO_TEMPLATE.replace("{{tagList}}", tagListStr);
//        result.append(intro);
//
//        // 각 트레이너에 대한 카드 생성
//        for (TrainerDTO trainer : trainers) {
//            // 프로필 이미지 - 없으면 기본 이미지 사용
//            String profileImage = (trainer.profileImageUrl() != null && !trainer.profileImageUrl().isEmpty())
//                    ? trainer.profileImageUrl()
//                    : "https://via.placeholder.com/150?text=" + trainer.nickname();
//
//            // 전문 분야 텍스트 - 없으면 태그 목록 사용
//            String specializationText = (trainer.specializationText() != null && !trainer.specializationText().isEmpty())
//                    ? trainer.specializationText()
//                    : (trainer.specializations() != null && !trainer.specializations().isEmpty()
//                    ? String.join(", ", trainer.specializations())
//                    : "다양한 분야");
//
//            // 자격증 정보 포맷팅
//            StringBuilder certBuilder = new StringBuilder();
//            if (trainer.certifications() != null && !trainer.certifications().isEmpty()) {
//                for (CertificationDTO cert : trainer.certifications()) {
//                    certBuilder.append("- ").append(cert.certName());
//                    if (cert.issuingBody() != null && !cert.issuingBody().isEmpty()) {
//                        certBuilder.append(" (").append(cert.issuingBody()).append(")");
//                    }
//                    certBuilder.append("\n");
//                }
//            } else {
//                certBuilder.append("- 자격증 정보가 없습니다.\n");
//            }
//
//            // 카드 템플릿 적용
//            String card = TRAINER_CARD_TEMPLATE
//                    .replace("{{nickname}}", nullSafeString(trainer.nickname(), "익명 트레이너"))
//                    .replace("{{profileImageUrl}}", profileImage)
//                    .replace("{{specializationText}}", specializationText)
//                    .replace("{{experienceYears}}", String.valueOf(trainer.experienceYears()))
//                    .replace("{{visitingAreas}}", nullSafeString(trainer.visitingAreas(), "문의 필요"))
//                    .replace("{{averageRating}}", String.format("%.1f", trainer.averageRating()))
//                    .replace("{{reviewCount}}", String.valueOf(trainer.reviewCount()))
//                    .replace("{{introduction}}", nullSafeString(trainer.introduction(), "소개 정보가 없습니다."))
//                    .replace("{{representativeCareer}}", nullSafeString(trainer.representativeCareer(), "경력 정보가 없습니다."))
//                    .replace("{{certifications}}", certBuilder.toString());
//
//            result.append(card);
//        }
//
//        // 마무리 문구 추가
//        result.append("\n질문하신 내용에 더 구체적인 도움이 필요하시면 언제든지 물어봐주세요! 다른 키워드로 검색하시려면 새로운 질문을 입력해주세요.");
//
//        return result.toString();
//    }
//
//    /**
//     * null 값 처리를 위한 유틸리티 메서드
//     */
//    private String nullSafeString(String value, String defaultValue) {
//        return (value != null && !value.trim().isEmpty()) ? value : defaultValue;
//    }
}