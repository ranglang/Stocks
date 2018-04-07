package stockDealing;

import util.Util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class 杜老师短线模型 {

	//399006创业板指； 399102创业板综； 999999上证综指； 399001深证成指； 399106深证综指
	int 日期 = 0;
	int 开盘价 = 1;
	int 最高价 = 2;
	int 最低价 = 3;
	int 收盘价 = 4;
	int 成交量 = 5;
	int 成交额 = 6;

	public void shortModel() throws IOException, ParseException {
		Util util = new Util();
		List<String[]> stockNums = util.getStockNum();
		double win = 0;
		double lose = 0;
		for (String[] stockNum : stockNums) {
//			System.out.println(stockNum[0] + stockNum[1]);
			HashMap<String, List<String[]>> singleStock = util.getSingleStock(stockNum[0]);
			//成交量放大到N倍，股价创历史新高
			int N日平均 = 5;            //五日平均成交量
			int 历史新高天数 = 120;    //历史180天新高
			double 量比 = 2;            //2倍成交量为放量
			boolean flag;
			//日线
			List<String[]> days = (List<String[]>) new ArrayList(singleStock.values()).get(0);
			//平均成交量线
			HashMap<Date, Double> vol5Line = (HashMap<Date, Double>) (new ArrayList(util.averageParam(singleStock, 成交量, N日平均).values())).get(0);
			//todo-上市不到一年的剔除
			if (days.size() < 360) {
				continue;
			}
			//与平均成交量线对比，同时对比是否是历史新高（此处可以优化为具有波峰形态的新高？）
			for (int i = 360; i < days.size(); i++) {
				String[] day = days.get(i);
				//时间格式化
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
				if (day[日期].contains("通达信")) {
					break;
				}
				Date date = simpleDateFormat.parse(day[日期]);
				//上证综指或深圳综指或创业板指下跌日内上涨的股票

				//上涨的股票
				//是否为历史新高
				double max;
				max = Double.valueOf(day[收盘价]);
				double temp;
				double vol5value;
				for (int j = 0; j < 历史新高天数; j++) {
					if ((temp = Double.valueOf(days.get(i - j)[收盘价])) > max) {
						max = temp;
					}
				}
				if (max > Double.valueOf(day[收盘价])) {
					flag = false;
				} else {
					//是历史新高的
					//向前推N天，获取成交量并比较是否为放量
					Calendar calen = Calendar.getInstance();
					calen.setTime(date);
					calen.add(Calendar.DAY_OF_YEAR, -1);
					Date vol5 = calen.getTime();
					while (vol5Line.get(vol5) == null) {
						calen.add(Calendar.DAY_OF_YEAR, -1);
						vol5 = calen.getTime();
					}
					vol5value = vol5Line.get(vol5);
					if (vol5value * 量比 <= Double.valueOf(day[成交量])) {
						flag = true;
					} else {
						flag = false;
					}
				}

				if (flag) {
					//次日红柱还是绿柱,减2除去最后一行文字
					if (i == days.size() - 2) {
						System.out.println(stockNum[0] + stockNum[1]);
						System.out.println("预测到了:"+simpleDateFormat.format(date));
						break;
					}

					if (Double.valueOf(days.get(i + 1)[收盘价]) - Double.valueOf(days.get(i + 1)[开盘价]) > 0) {
						win++;
					} else {
						lose++;
					}
					//输出16年以后的有效结果
//					if (date.after(simpleDateFormat.parse("2016/1/1"))) {
//						System.out.println(simpleDateFormat.format(date));
//					}
				}
			}
		}
		System.out.println("胜率是：" + win / (win + lose));
	}
}
