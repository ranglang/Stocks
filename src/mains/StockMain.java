package mains;

import spider.xueqiuSpider;
import stockDealing.杜老师短线模型;
import stockDealing.连板后上涨;

import java.io.IOException;
import java.text.ParseException;

public class StockMain {

	public static void 连板上涨Main() throws IOException, InterruptedException {
		xueqiuSpider xueqiuSpider = new xueqiuSpider();
		连板后上涨 limit = new 连板后上涨();
		xueqiuSpider.getFundholding("F:\\雪球基金持股.txt");
		limit.dealFundHolding("F:\\雪球基金持股.txt");
	}

	public static void 杜老师短线模型Main() throws IOException, ParseException {
		杜老师短线模型 runShortModel = new 杜老师短线模型();
		runShortModel.shortModel();
	}

	public static void main(String[] args) throws IOException, InterruptedException, ParseException {
//		连板上涨Main();
		杜老师短线模型Main();
	}
}
