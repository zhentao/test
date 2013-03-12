package com.zhentao.test.hadoop;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.lib.MultipleTextOutputFormat;

public class MultiOutput {
    public static void main(String[] args) throws IOException {
        JobConf conf = new JobConf(MultiOutput.class);
        conf.setJobName("Multioutput");
        FileInputFormat.setInputPaths(conf, new Path(args[0]));
        FileOutputFormat.setOutputPath(conf, new Path(args[1]));
        conf.setMapperClass(MyMapper.class);
        conf.setMapOutputKeyClass(LongWritable.class);
        conf.setMapOutputValueClass(Text.class);
        conf.setReducerClass(MyReducer.class);
        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(Text.class);
        conf.setOutputFormat(MyMultiOutputFormat.class);
        JobClient.runJob(conf);
    }

    private static class MyMapper extends MapReduceBase implements Mapper<LongWritable, Text, LongWritable, Text> {

        @Override
        public void map(LongWritable key, Text value, OutputCollector<LongWritable, Text> output, Reporter reporter)
                throws IOException {
            output.collect(key, value);
        }
    }

    private static class MyReducer extends MapReduceBase implements Reducer<LongWritable, Text, Text, Text> {

        @Override
        public void reduce(LongWritable key, Iterator<Text> values, OutputCollector<Text, Text> output,
                Reporter reporter) throws IOException {
            StringBuilder data = new StringBuilder();
            while (values.hasNext()) {
                data.append(values.next().toString());
            }
            output.collect(new Text("abc"), new Text(data.toString()));
            output.collect(new Text("xyz"), new Text(data.toString()));

        }
    }

    private static class MyMultiOutputFormat<K, V> extends MultipleTextOutputFormat<Text, Text> {
        @Override
        protected String generateFileNameForKeyValue(Text key, Text value, String name) {
            return key.toString() + "/" + name;
        }

        @Override
        protected Text generateActualKey(Text key, Text value) {
            return null;
        }

        @Override
        protected String generateLeafFileName(String name) {
            return "alex" + name.substring(4);
          }
    }
}
