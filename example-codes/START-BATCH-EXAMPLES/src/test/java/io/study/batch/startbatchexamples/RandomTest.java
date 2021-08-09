package io.study.batch.startbatchexamples;

import java.util.Random;

import org.junit.jupiter.api.Test;

public class RandomTest {

	@Test
	void 테스트_랜덤(){
		Random r = new Random();
		int result = r.nextInt(2) + 1;
		System.out.println("랜덤 :: " + result);
	}
}
