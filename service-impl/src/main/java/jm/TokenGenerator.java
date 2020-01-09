package jm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class TokenGenerator {
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String PUNCTUATION = "!@#$%&*()_+-=[]|,./?><";
    private boolean useLower;
    private boolean useUpper;
    private boolean useDigits;
    private boolean usePunctuation;

    private TokenGenerator() {
        throw new UnsupportedOperationException("Empty constructor is not supported.");
    }

    private TokenGenerator(TokenGeneratorBuilder builder) {
        this.useLower = builder.useLower;
        this.useUpper = builder.useUpper;
        this.useDigits = builder.useDigits;
        this.usePunctuation = builder.usePunctuation;
    }

    public String generate(int length) {

        // Argument Validation.
        if (length <= 0) { return ""; }

        // Variables.
        StringBuilder password = new StringBuilder(length);
        Random random = new Random(System.nanoTime());

        // Collect the categories to use.
        List<String> charCategories = new ArrayList<>(4);
        if (useLower) { charCategories.add(LOWER); }
        if (useUpper) { charCategories.add(UPPER); }
        if (useDigits) { charCategories.add(DIGITS); }
        if (usePunctuation) { charCategories.add(PUNCTUATION); }

        // Build the password.
        for (int i = 0; i < length; i++) {
            String charCategory = charCategories.get(random.nextInt(charCategories.size()));
            int position = random.nextInt(charCategory.length());
            password.append(charCategory.charAt(position));
        }
        return new String(password);
    }

    public static class TokenGeneratorBuilder {
        private boolean useLower;
        private boolean useUpper;
        private boolean useDigits;
        private boolean usePunctuation;

        public TokenGeneratorBuilder() {
            this.useLower = false;
            this.useUpper = false;
            this.useDigits = false;
            this.usePunctuation = false;
        }

        public TokenGeneratorBuilder useLower(boolean useLower) {
            this.useLower = useLower;
            return this;
        }

        public TokenGeneratorBuilder useUpper(boolean useUpper) {
            this.useUpper = useUpper;
            return this;
        }

        public TokenGeneratorBuilder useDigits(boolean useDigits) {
            this.useDigits = useDigits;
            return this;
        }

        public TokenGeneratorBuilder usePunctuation(boolean usePunctuation) {
            this.usePunctuation = usePunctuation;
            return this;
        }

        public TokenGenerator build() {
            return new TokenGenerator(this);
        }
    }
}