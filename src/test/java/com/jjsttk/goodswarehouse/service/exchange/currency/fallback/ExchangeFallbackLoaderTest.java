package com.jjsttk.goodswarehouse.service.exchange.currency.fallback;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjsttk.goodswarehouse.service.exchange.request.ExchangeData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExchangeFallbackLoaderTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ResourceLoader resourceLoader;

    @Mock
    private Resource resource;

    @InjectMocks
    private ExchangeFallbackLoader sut;

    @Test
    void getFallBackDataFromFileShouldReturnExchangeDataWhenFileExistsAndValid() throws Exception {
        var fileName = "fallback/rates.json";
        var fullPath = "classpath:" + fileName;

        var expectedData = ExchangeData.builder()
                .rates(Map.of(
                        com.jjsttk.goodswarehouse.enums.PriceCurrency.USD, new BigDecimal("90.00"),
                        com.jjsttk.goodswarehouse.enums.PriceCurrency.EUR, new BigDecimal("95.00")
                ))
                .build();

        when(resourceLoader.getResource(fullPath)).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        when(resource.getInputStream()).thenReturn(new ByteArrayInputStream("{}".getBytes()));
        when(objectMapper.readValue(any(InputStream.class), eq(ExchangeData.class))).thenReturn(expectedData);

        var result = sut.getFallBackDataFromFile(fileName);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedData);
        verify(resourceLoader).getResource(fullPath);
        verify(resource).exists();
        verify(resource).getInputStream();
    }

    @Test
    void getFallBackDataFromFileShouldThrowRuntimeExceptionWhenFileNotFound() throws IOException {
        var fileName = "nonexistent.json";
        var fullPath = "classpath:" + fileName;

        when(resourceLoader.getResource(fullPath)).thenReturn(resource);
        when(resource.exists()).thenReturn(false);

        assertThatThrownBy(() -> sut.getFallBackDataFromFile(fileName))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Fallback file not found: " + fileName);

        verify(resourceLoader).getResource(fullPath);
        verify(resource).exists();
        verify(resource, never()).getInputStream();
    }

    @Test
    void getFallBackDataFromFileShouldThrowRuntimeExceptionWhenIOExceptionOccurs() throws Exception {
        var fileName = "corrupted.json";
        var fullPath = "classpath:" + fileName;

        when(resourceLoader.getResource(fullPath)).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        when(resource.getInputStream()).thenThrow(new IOException("File corrupted"));

        assertThatThrownBy(() -> sut.getFallBackDataFromFile(fileName))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Cannot fetch exchange rates from fallback file: " + fileName)
                .hasCauseInstanceOf(IOException.class)
                .hasRootCauseMessage("File corrupted");

        verify(resourceLoader).getResource(fullPath);
        verify(resource).exists();
        verify(resource).getInputStream();
    }

    @Test
    void getFallBackDataFromFileShouldThrowRuntimeExceptionWhenJacksonParsingFails() throws Exception {
        var fileName = "invalid-format.json";
        var fullPath = "classpath:" + fileName;

        when(resourceLoader.getResource(fullPath)).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        when(resource.getInputStream()).thenReturn(new ByteArrayInputStream("{}".getBytes()));
        when(objectMapper.readValue(any(InputStream.class), eq(ExchangeData.class)))
                .thenThrow(new IOException("JSON parsing error"));

        assertThatThrownBy(() -> sut.getFallBackDataFromFile(fileName))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Cannot fetch exchange rates from fallback file: " + fileName)
                .hasCauseInstanceOf(IOException.class)
                .hasRootCauseMessage("JSON parsing error");
    }

    @Test
    void getFallBackDataFromFileShouldUseClasspathPrefix() throws Exception {
        var fileName = "rates.json";
        var fullPath = "classpath:" + fileName;

        var expectedData = ExchangeData.builder()
                .rates(Map.of(com.jjsttk.goodswarehouse.enums.PriceCurrency.USD, BigDecimal.ONE))
                .build();

        when(resourceLoader.getResource(fullPath)).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        when(resource.getInputStream()).thenReturn(new ByteArrayInputStream("{}".getBytes()));
        when(objectMapper.readValue(any(InputStream.class), eq(ExchangeData.class))).thenReturn(expectedData);

        var result = sut.getFallBackDataFromFile(fileName);

        assertThat(result).isNotNull();
        verify(resourceLoader).getResource(fullPath);
    }

    @Test
    void getFallBackDataFromFileShouldWorkWithNestedPaths() throws Exception {
        var fileName = "config/fallback/exchange-rates.json";
        var fullPath = "classpath:" + fileName;

        var expectedData = ExchangeData.builder()
                .rates(Map.of(
                        com.jjsttk.goodswarehouse.enums.PriceCurrency.CNY, new BigDecimal("12.50"),
                        com.jjsttk.goodswarehouse.enums.PriceCurrency.EUR, new BigDecimal("0.92")
                ))
                .build();

        when(resourceLoader.getResource(fullPath)).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        when(resource.getInputStream()).thenReturn(new ByteArrayInputStream("{}".getBytes()));
        when(objectMapper.readValue(any(InputStream.class), eq(ExchangeData.class))).thenReturn(expectedData);

        var result = sut.getFallBackDataFromFile(fileName);

        assertThat(result).isNotNull();
        assertThat(result.rates()).hasSize(2);
        verify(resourceLoader).getResource(fullPath);
    }

    @Test
    void getFallBackDataFromFileShouldHandleEmptyFileName() {
        var fileName = "";
        var fullPath = "classpath:";

        when(resourceLoader.getResource(fullPath)).thenReturn(resource);
        when(resource.exists()).thenReturn(false);

        assertThatThrownBy(() -> sut.getFallBackDataFromFile(fileName))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Fallback file not found: " + fileName);
    }

    @Test
    void getFallBackDataFromFileShouldHandleFileNameWithSpaces() throws Exception {
        var fileName = "fallback rates.json";
        var fullPath = "classpath:" + fileName;

        var expectedData = ExchangeData.builder()
                .rates(Map.of(com.jjsttk.goodswarehouse.enums.PriceCurrency.USD, BigDecimal.ONE))
                .build();

        when(resourceLoader.getResource(fullPath)).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        when(resource.getInputStream()).thenReturn(new ByteArrayInputStream("{}".getBytes()));
        when(objectMapper.readValue(any(InputStream.class), eq(ExchangeData.class))).thenReturn(expectedData);

        var result = sut.getFallBackDataFromFile(fileName);

        assertThat(result).isNotNull();
        verify(resourceLoader).getResource(fullPath);
    }
}
