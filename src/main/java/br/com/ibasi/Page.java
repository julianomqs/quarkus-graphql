package br.com.ibasi;

import java.util.List;

public record Page<T>(int total, List<T> results) {
}
