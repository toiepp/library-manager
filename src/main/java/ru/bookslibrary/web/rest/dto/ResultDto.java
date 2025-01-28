package ru.bookslibrary.web.rest.dto;

public class ResultDto {

    private String message;

    public ResultDto(String message) {
        this.message = message;
    }

    public ResultDto() {}

    public static ResultDtoBuilder builder() {
        return new ResultDtoBuilder();
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ResultDto)) return false;
        final ResultDto other = (ResultDto) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$message = this.getMessage();
        final Object other$message = other.getMessage();
        if (this$message == null ? other$message != null : !this$message.equals(other$message)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ResultDto;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $message = this.getMessage();
        result = result * PRIME + ($message == null ? 43 : $message.hashCode());
        return result;
    }

    public String toString() {
        return "ResultDto(message=" + this.getMessage() + ")";
    }

    public static class ResultDtoBuilder {

        private String message;

        ResultDtoBuilder() {}

        public ResultDtoBuilder message(String message) {
            this.message = message;
            return this;
        }

        public ResultDto build() {
            return new ResultDto(this.message);
        }

        public String toString() {
            return "ResultDto.ResultDtoBuilder(message=" + this.message + ")";
        }
    }
}
