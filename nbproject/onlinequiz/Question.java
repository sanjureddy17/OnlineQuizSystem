package onlinequiz;

public class Question {
    private int id;
    private String question, option1, option2, option3, option4;
    private int answer;
    private String ageGroup;
    private String level;

    // Constructor with all fields
    public Question(int id, String question, String option1, String option2,
                    String option3, String option4, int answer,
                    String ageGroup, String level) {
        this.id = id;
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.answer = answer;
        this.ageGroup = ageGroup;
        this.level = level;
    }

    // Constructor without id (useful when inserting new question)
    public Question(String question, String option1, String option2,
                    String option3, String option4, int answer,
                    String ageGroup, String level) {
        this(0, question, option1, option2, option3, option4, answer, ageGroup, level);
    }

    // --- Getters ---
    public int getId() { return id; }
    public String getQuestion() { return question; }
    public String getOption1() { return option1; }
    public String getOption2() { return option2; }
    public String getOption3() { return option3; }
    public String getOption4() { return option4; }
    public int getAnswer() { return answer; }
    public String getAgeGroup() { return ageGroup; }
    public String getLevel() { return level; }

    // --- Setters ---
    public void setId(int id) { this.id = id; }
    public void setQuestion(String question) { this.question = question; }
    public void setOption1(String option1) { this.option1 = option1; }
    public void setOption2(String option2) { this.option2 = option2; }
    public void setOption3(String option3) { this.option3 = option3; }
    public void setOption4(String option4) { this.option4 = option4; }
    public void setAnswer(int answer) { this.answer = answer; }
    public void setAgeGroup(String ageGroup) { this.ageGroup = ageGroup; }
    public void setLevel(String level) { this.level = level; }

    // --- Display in JList ---
    @Override
    public String toString() {
        return "[ID: " + id + "] " + question + " (Age: " + ageGroup + ", Level: " + level + ")";
    }

    // --- Method used by QuizFrame ---
    public String getQuestionText() {
        return question;
    }
}
