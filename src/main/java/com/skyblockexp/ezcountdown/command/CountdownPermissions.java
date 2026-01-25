package com.skyblockexp.ezcountdown.command;

public record CountdownPermissions(String base,
                                   String create,
                                   String start,
                                   String stop,
                                   String delete,
                                   String list,
                                   String info,
                                   String reload) {
}
