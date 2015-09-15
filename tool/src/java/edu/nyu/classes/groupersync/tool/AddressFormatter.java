package edu.nyu.classes.groupersync.tool;

class AddressFormatter {

    public static String format(String grouperId) {
        return grouperId.replace(":", "-") + "@nyu.edu";
    }

}
