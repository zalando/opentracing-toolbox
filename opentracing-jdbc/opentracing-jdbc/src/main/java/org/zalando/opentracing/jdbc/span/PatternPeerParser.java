package org.zalando.opentracing.jdbc.span;

import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

final class PatternPeerParser implements PeerParser {

    private final Pattern pattern = compile(
            "jdbc:(?:[a-z0-9-]+:)+(?:/{2}|@|)(?<host>(?:\\[.+])|[^:/]+)(?::(?<port>\\d+))?.*");

    /**
     * @see <a href="https://nbviewer.jupyter.org/github/rasbt/python_reference/blob/master/tutorials/useful_regex.ipynb#Checking-for-IP-addresses">A collection of useful regular expressions</a>
     */
    private final Pattern ipv4 = compile(
            "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)");

    /**
     * Added requirement for surrounding square brackets.
     *
     * @see <a href="https://nbviewer.jupyter.org/github/rasbt/python_reference/blob/master/tutorials/useful_regex.ipynb#Checking-for-IP-addresses">A collection of useful regular expressions</a>
     */
    private final Pattern ipv6 = compile(
            "\\[((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:)))(%.+)?]");

    @Override
    public Peer parse(final String url) {
        final Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            return createFrom(matcher);
        }

        return Peer.EMPTY;
    }

    private Peer createFrom(final Matcher matcher) {
        final String host = matcher.group("host");
        @Nullable final String port = matcher.group("port");

        final Peer url = Peer.EMPTY;
        return withPort(withHost(url, host), port);
    }

    private Peer withHost(final Peer url, final String host) {
        if (isIpv4(host)) {
            return url.withIpv4(host);
        }

        if (isIpv6(host)) {
            return url.withIpv6(removeSquareBrackets(host));
        }

        return url.withHostname(host);
    }

    private boolean isIpv4(final String host) {
        return ipv4.matcher(host).matches();
    }

    private boolean isIpv6(final String host) {
        return ipv6.matcher(host).matches();
    }

    private String removeSquareBrackets(final String ipv6) {
        return ipv6.substring(1, ipv6.length() - 1);
    }

    private Peer withPort(final Peer url, @Nullable final String port) {
        if (port == null) {
            return url;
        }

        return url.withPort(Integer.valueOf(port));
    }

}
