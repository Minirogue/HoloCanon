//
//  ContentView.swift
//  holocanon
//
//  Created by Shawn Witte on 6/6/25.
//

import Foundation
import SwiftUI
import shared

struct ContentView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        return MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
    }
}
