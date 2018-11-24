package crispy_octo_moo.consts;

/**
 * Created by yangboz on 9/23/15.
 */
public enum SocialProviders {


    FACEBOOK("facebook"), LINKEDIN("linkedin");

    private String _value;

    SocialProviders(final String value) {
        this._value = value;
    }

    public String getValue() {
        return _value;
    }
}
